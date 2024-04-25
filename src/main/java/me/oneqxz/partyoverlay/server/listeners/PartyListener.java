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
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.sctructures.Party;
import me.oneqxz.partyoverlay.server.sctructures.PartyMember;

import java.util.Arrays;

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
            // TODO: notify the user that he/she is already in a party
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
            return;

        Party party = PartyManager.getInstance().getPartyByConnectedUser(user);
        if(party == null)
            return;

        if(Arrays.stream(user.getUser().getFriends()).noneMatch(fr -> fr.getId() == friend.getUser().getId()))
            return;

        if(PartyInviteManager.getInstance().getPartyInvites().stream()
                .anyMatch(invite -> invite.getInviter().getUser().getId() == invite.getInvited().getUser().getId()))
            return;

        PartyInviteManager.getInstance().proceedPartyInviteAdd(user, friend, party);
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
}
