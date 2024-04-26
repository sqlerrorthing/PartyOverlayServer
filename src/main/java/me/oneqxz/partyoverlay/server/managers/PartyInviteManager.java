package me.oneqxz.partyoverlay.server.managers;

import lombok.Getter;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SMemberPartyJoin;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SNewInvite;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SPartyInviteResult;
import me.oneqxz.partyoverlay.server.sctructures.*;
import me.oneqxz.partyoverlay.server.utils.LinkedSet;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 23.04.2024
 */
@Getter
public class PartyInviteManager {

    private static PartyInviteManager INSTANCE;
    private final Set<PartyInvite> partyInvites = new LinkedSet<>();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(5);

    private PartyInviteManager() {

    }

    public void proceedPartyInviteAdd(ConnectedUser inviter, ConnectedUser invited, Party party, Responder responder)
    {
        if(partyInvites.stream().anyMatch(
                invite -> invite.getInvited().getUser().getId() == invited.getUser().getId() &&
                        (invite.getPartyUUID().equals(party.getPartyUUID()))
        )) {
            responder.respond(new SPartyInviteResult(
                    InviteResult.ALREADY_INVITED
            ));
            return;
        }

        if(party.getMembers().stream().anyMatch(member -> member.getUser().getUser().getId() == invited.getUser().getId()))
        {
            responder.respond(new SPartyInviteResult(
                    InviteResult.ALREADY_ON_PARTY
            ));
            return;
        }

        PartyInvite invite = new PartyInvite(
                inviter, invited, party.getPartyUUID()
        );

        responder.respond(new SPartyInviteResult(
                InviteResult.INVITED
        ));

        this.partyInvites.add(invite);

        invited.getCtx().writeAndFlush(new SNewInvite(
                party.getPartyUUID(),
                inviter.getUser().getUsername(),
                inviter.getMinecraftUser().getUsername()
        ));

        invite.start(executor, 0, 50, TimeUnit.MILLISECONDS);

    }

    public void proceedPartyInvite(PartyInvite partyInvite)
    {
        if(Arrays.stream(partyInvite.getInviter().getUser().getFriends()).noneMatch(fr -> fr.getId() == partyInvite.getInvited().getUser().getId())) {
            this.proceedPartyInviteRemove(partyInvite);
            return;
        }

        if(partyInvite.getInvited().isOnParty())
            PartyManager.getInstance().proceedPartyLeave(partyInvite.getInvited());

        Party currentParty = partyInvite.getInviter().getUserParty();
        if(currentParty == null)
            currentParty = PartyManager.getInstance().createParty(partyInvite.getInviter(), partyInvite.getInviter().getUser().getUsername() + "'s party");

        if(currentParty.getPartyOwner().getUser().getUser().getId() != partyInvite.getInviter().getUser().getId())
        {
            this.proceedPartyInviteRemove(partyInvite);
            return;
        }

        PartyMember member = PartyManager.getInstance().proceedPartyJoin(currentParty, partyInvite.getInvited());

        currentParty.getMembers()
                .stream().filter(m -> m.getUser().getUser().getId() != partyInvite.getInvited().getUser().getId())
                .forEach(m -> m.getUser().getCtx().writeAndFlush(new SMemberPartyJoin(
                        member.getUser().getUser().getId(),
                        member.getUser().getUser().getUsername(),
                        member.getUser().getMinecraftUser().getUsername(),
                        member.getPlayerColor()
        )));

        this.proceedPartyInviteRemove(partyInvite);
    }

    public void proceedPartyInviteRemove(PartyInvite partyInvite)
    {
        this.partyInvites.remove(partyInvite);
        partyInvite.stop();
    }

    public void proceedPartyDisbanded(Party party)
    {
        this.partyInvites.removeIf(partyInvite -> partyInvite.getPartyUUID() == party.getPartyUUID());
    }

    public void proceedPartyOwnerChange(Party party, ConnectedUser oldOwner)
    {
        this.partyInvites.removeIf(partyInvite ->
                partyInvite.getPartyUUID() == party.getPartyUUID() && party.getPartyOwner().getUser().getUser().getId() == oldOwner.getUser().getId());
    }

    public Set<PartyInvite> getPartyInvitesForConnectedUser(ConnectedUser user)
    {
        return this.partyInvites.stream().filter(invite -> invite.getInvited().getUser().getId() == user.getUser().getId())
                .collect(Collectors.toSet());
    }

    public static PartyInviteManager getInstance()
    {
        return INSTANCE == null ? INSTANCE = new PartyInviteManager() : INSTANCE;
    }

}
