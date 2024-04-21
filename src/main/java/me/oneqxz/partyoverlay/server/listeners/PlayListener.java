package me.oneqxz.partyoverlay.server.listeners;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CStartPlaying;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CStopPlaying;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Log4j2
public class PlayListener {

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPacketStartPlaying(CStartPlaying packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        user.setServerData(packet.getServerData());
        log.info("User {} start playing on {}", user.getUser().getUsername(), packet.getServerData().getServerIP());
    }

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPacketStopPlaying(CStopPlaying ignored, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        user.setServerData(null);
        log.info("User {} stopped playing", user.getUser().getUsername());
    }
}
