package me.oneqxz.partyoverlay.server.sctructures;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.Settings;
import me.oneqxz.partyoverlay.server.managers.PartyInviteManager;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SAddPing;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SPartySync;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SRemovePing;
import me.oneqxz.partyoverlay.server.utils.LinkedSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Getter
@RequiredArgsConstructor
@Setter
@Log4j2
public class Party implements Runnable {

    @NonNull private UUID partyUUID;
    @NonNull private String partyName;
    private List<PartyMember> members = new LinkedList<>();
    private Set<Ping> pings = new LinkedSet<>();

    private ScheduledFuture<?> scheduledFuture;

    public PartyMember getPartyOwner()
    {
        return this.members.stream().filter(PartyMember::isOwner).findFirst().orElse(null);
    }

    public PartyMember getFirstPartyMember()
    {
        return this.members.stream().findFirst().orElse(null);
    }

    public void removeConnectedUser(ConnectedUser user)
    {
        this.members.removeIf(member -> member.getUser() == user);
    }

    public void removeConnectedMember(PartyMember member)
    {
        this.removePing(this.getMemberPing(member));
        this.members.remove(member);
    }

    public PartyMember connectedUserToPartyMember(ConnectedUser user)
    {
        return this.members.stream().filter(member -> member.getUser() == user).findFirst().orElse(null);
    }

    public void start(ScheduledExecutorService executor, long initialDelay, long period, TimeUnit unit) {
        scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, period, unit);
    }

    public void stop() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }

        for(Ping ping : pings)
            this.sendToAll(new SRemovePing(ping.getPingUUID()));
    }

    public Ping getMemberPing(PartyMember member)
    {
        return this.pings.stream().filter(f -> f.getFrom().getUser().getUser().getId() == member.getUser().getUser().getId()).findFirst().orElse(null);
    }

    public void addPing(Ping ping)
    {
        this.pings.add(ping);
        this.sendToAll(new SAddPing(
                ping.getPingUUID(),
                ping.getX(),
                ping.getY(),
                ping.getZ(),
                ping.getFrom().getUser().getUser().getId()
        ));
    }

    public void removePing(Ping ping)
    {
        if(ping != null)
        {
            this.pings.remove(ping);
            this.sendToAll(new SRemovePing(
                    ping.getPingUUID()
            ));
        }
    }

    public void updatePings()
    {
        for(Ping ping : this.pings)
            if(System.currentTimeMillis() > ping.getCreated() + Settings.PING_LIFETIME)
                this.removePing(ping);
    }

    public void sendToAll(Packet packet)
    {
        for(PartyMember member : members)
            if(member.getUser().getCtx().channel().isOpen())
                member.getUser().getCtx().writeAndFlush(packet);
    }

    @Override
    public void run() {
        this.updatePings();

        PartyMember[] members = this.members.toArray(new PartyMember[0]);
        SPartySync packet = new SPartySync(
                this.partyUUID,
                this.partyName,
                members,
                PartyInviteManager.getInstance().getPartyInvites().stream().filter(invite -> invite.getPartyUUID() == this.partyUUID).toArray(PartyInvite[]::new)
        );

        this.sendToAll(packet);
    }
}
