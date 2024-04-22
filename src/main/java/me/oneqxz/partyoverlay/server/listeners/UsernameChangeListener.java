package me.oneqxz.partyoverlay.server.listeners;

import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CMinecraftUsernameChanged;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Log4j2
public class UsernameChangeListener {

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onPacketChangeMinecraftUsername(CMinecraftUsernameChanged packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        if(packet.getMinecraftUsername().matches("^[a-zA-Z_]{3,16}$"))
            user.getMinecraftUser().setUsername(packet.getMinecraftUsername());
    }

}
