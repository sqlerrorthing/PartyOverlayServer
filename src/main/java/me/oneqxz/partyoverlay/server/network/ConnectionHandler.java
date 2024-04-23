package me.oneqxz.partyoverlay.server.network;

import com.j256.ormlite.dao.Dao;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.database.DatabaseConnection;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.managers.PartyManager;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SRequireLogin;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.utils.TimeUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
@Log4j2
public class ConnectionHandler extends ChannelInboundHandlerAdapter {

    @Getter protected static HashMap<UUID, ChannelHandlerContext> connected = new HashMap<>();
    @Getter protected static List<ConnectedUser> connectedUsers = new LinkedList<>();
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ctx.pipeline().addLast(new ReadTimeoutHandler(5));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UUID uuid = UUID.randomUUID();
        connected.put(uuid, ctx);

        log.info("New connection");
        ctx.channel().writeAndFlush(new SRequireLogin(uuid));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        connected.values().remove(ctx);
        log.info("Removing connection: {}", ctx);

        ConnectedUser user = getUserByCTX(ctx);
        if(user != null)
            removeUser(user);

        ctx.close();
        super.channelInactive(ctx);
    }

    public static ConnectedUser getUserByCTX(ChannelHandlerContext ctx) {
        return connectedUsers.stream().filter(user -> ctx.channel().id().asLongText().equals(user.getCtx().channel().id().asLongText())).findFirst().orElse(null);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        ctx.fireChannelInactive();
        log.error("User has error", cause);
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
        user.start(executor, 5000, 1000, TimeUnit.MILLISECONDS);
    }

    public static void removeUser(ConnectedUser user)
    {
        log.info("Removed user {}", user.getUser().getUsername());
        PartyManager.getInstance().proceedPartyLeave(user);
        connectedUsers.remove(user);
        user.stop();

        try
        {
            Dao<User, Integer> userDao = DatabaseConnection.getInstance().getUsersDao();
            user.getUser().setLastOnline(TimeUtils.getUTCMillis());
            userDao.update(user.getUser());
        }
        catch (Exception e)
        {
            log.error(e);
        }
    }
}
