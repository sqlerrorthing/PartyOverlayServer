package me.oneqxz.partyoverlay.server.sctructures;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import me.oneqxz.partyoverlay.server.database.models.User;

import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@AllArgsConstructor
@Builder
@Data
public class ConnectedUser {
    private UUID uuid;
    private ChannelHandlerContext ctx;
    private String minecraftUsername;
    private String currentServer;
    private boolean isPlaying;
    private User user;

}
