package me.oneqxz.partyoverlay.server.network;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SRequireLogin;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@ChannelHandler.Sharable
@Log4j2
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    @Getter protected static HashMap<UUID, ChannelHandlerContext> connected = new HashMap<>();
    @Getter protected static List<ConnectedUser> connectedUsers = new LinkedList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UUID uuid = UUID.randomUUID();
        connected.put(uuid, ctx);

        log.info("New connection, sending SRequireLogin");
        ctx.channel().writeAndFlush(new SRequireLogin(uuid));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connected.values().remove(ctx);
        connectedUsers.remove(getUserByCTX(ctx));
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        log.error("User has error", cause);
    }

    public static ConnectedUser getUserByCTX(ChannelHandlerContext ctx) {
        return connectedUsers.stream().filter(user -> ctx.channel() == user.getCtx().channel()).findFirst().orElse(null);
    }

    public static ConnectedUser getUserByID(int connectedID)
    {
        return connectedUsers.stream().filter(user -> user.getUser().getId() == connectedID).findFirst().orElse(null);
    }

    public static boolean isConnected(ChannelHandlerContext ctx) {
        return getUserByCTX(ctx) != null;
    }

    public static boolean isConnected(int id) {
        return getUserByID(id) != null;
    }

    public static void connectUser(ConnectedUser user)
    {
        connectedUsers.add(user);
    }
}
