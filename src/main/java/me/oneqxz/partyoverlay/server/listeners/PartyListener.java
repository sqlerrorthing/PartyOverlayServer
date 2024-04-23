package me.oneqxz.partyoverlay.server.listeners;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.managers.PartyManager;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CPartyCreate;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CPartySync;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.sctructures.PartyMember;

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
        PartyMember member = PartyManager.getInstance().getUserParty(user);
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

}
