package me.oneqxz.partyoverlay.server.listeners;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.managers.PartyInviteManager;
import me.oneqxz.partyoverlay.server.managers.PartyManager;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.*;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SPartyInviteResult;
import me.oneqxz.partyoverlay.server.sctructures.*;

import java.util.Arrays;
import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
public class PartyListener {

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPartyCreateReceived(CPartyCreate packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        if(PartyManager.getInstance().isOnParty(user))
        {
            return;
        }

        PartyManager.getInstance().createParty(user, packet.getPartyName());
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onClientPartySync(CPartySync packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        PartyMember member = PartyManager.getInstance().getPartyMember(user);
        if(member == null)
            return;

        member.setHealth(packet.getHealth());
        member.setMaxHealth(packet.getMaxHealth());

        member.setYaw(packet.getYaw());
        member.setPitch(packet.getPitch());

        member.setPosX(packet.getX());
        member.setPosY(packet.getY());
        member.setPosZ(packet.getZ());

        member.setHurtTime(packet.getHurtTime());
        member.setDimension(packet.getDimension());

        member.setMainHandItem(packet.getMainHandItem());
        member.setOffHandItem(packet.getOffHandItem());

        member.setHelmetItem(packet.getHelmetItem());
        member.setChestplateItem(packet.getChestplateItem());
        member.setLeggingsItem(packet.getLeggingsItem());
        member.setBootsItem(packet.getBootsItem());
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPartyLeave(CPartyLeave packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        PartyManager.getInstance().proceedPartyLeave(user);
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPlayerInviteFriend(CFriendPartyInvite packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        ConnectedUser friend = ConnectionHandler.getUserByID(packet.getFriendID());
        if(friend == null)
        {
            responder.respond(new SPartyInviteResult(
                InviteResult.FAIL
            ));
            return;
        }

        Party party = PartyManager.getInstance().getPartyByConnectedUser(user);
        if(party == null)
            party = PartyManager.getInstance().createParty(user, user.getUser().getUsername() + "'s party");

        if(Arrays.stream(user.getUser().getFriends()).noneMatch(fr -> fr.getId() == friend.getUser().getId()))
        {
            responder.respond(new SPartyInviteResult(
                    InviteResult.FAIL
            ));
            return;
        }

        if(PartyInviteManager.getInstance().getPartyInvites().stream()
                .anyMatch(invite -> invite.getInviter().getUser().getId() == invite.getInvited().getUser().getId()))
            return;

        PartyInviteManager.getInstance().proceedPartyInviteAdd(user, friend, party, responder);
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPlayerPartyInviteAccepted(CPartyInviteAccept packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        PartyManager.getInstance().proceedPartyJoinByInvite(user, packet.getPartyUUID());
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPlayerPartyInviteReject(CPartyInviteReject packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        PartyManager.getInstance().proceedPartyJoinByInviteReject(user, packet.getPartyUUID());
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPlayerAddPing(CPing packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        Party party = PartyManager.getInstance().getPartyByConnectedUser(user);
        if(party == null)
            return;

        PartyMember member = PartyManager.getInstance().getPartyMember(user);
        if(member == null)
            return;

        Ping memberPing = party.getMemberPing(member);
        party.removePing(memberPing);

        Ping ping = new Ping(
                UUID.randomUUID(),
                System.currentTimeMillis(),
                member,
                packet.getX(), packet.getY(), packet.getZ()
        );

        party.addPing(ping);
    }
}
