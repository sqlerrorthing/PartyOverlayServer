package me.oneqxz.partyoverlay.server.listeners;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.database.DatabaseConnection;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CLogin;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SConnected;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SDisconnect;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedMinecraftUser;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.utils.HashUtils;
import me.oneqxz.partyoverlay.server.utils.TimeUtils;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@Log4j2
public class LoginListener {

    @SneakyThrows
    @PacketSubscriber
    public void onLoginReceived(CLogin loginPacket, ChannelHandlerContext ctx, Responder responder)
    {
        ConnectionSource connection = DatabaseConnection.getInstance().getConnectionSource();
        Dao<User, Integer> userDao = DaoManager.createDao(connection, User.class);

        QueryBuilder<User, Integer> queryBuilder = userDao.queryBuilder();
        Where<User, Integer> where = queryBuilder.where();
        where.eq("username", loginPacket.getCredits().getUsername()).and().eq("password", HashUtils.hashPassword(loginPacket.getCredits().getPassword()));

        User user;
        try {
            user = queryBuilder.queryForFirst();
        }
        catch (Exception e)
        {
            user = null;
        }

        if(user == null)
        {
            disconnectWithReason(SDisconnect.Reason.INVALID_CREDITS, ctx, responder);
            connection.close();
            return;
        }

        if(ConnectionHandler.isConnected(user.getId()))
        {
            disconnectWithReason(SDisconnect.Reason.ALREADY_CONNECTED, ctx, responder);
            connection.close();
            return;
        }

        user.setIp(((InetSocketAddress) ctx.channel().remoteAddress()).getHostName());
        user.setLastOnline(TimeUtils.getUTCMillis());
        userDao.update(user);

        ConnectedUser connectedUser = ConnectedUser.builder()
                .user(user)
                .uuid(UUID.randomUUID())
                .minecraftUser(ConnectedMinecraftUser.builder()
                        .skin(new byte[0])
                        .username(loginPacket.getMinecraftUsername())
                        .serverData(loginPacket.getServerData())
                        .build())
                .ctx(ctx)
                .build();
        log.info(connectedUser.toString());
        log.debug("New login: {}, {}", loginPacket.getMinecraftUsername(), loginPacket.getCredits().getUsername());
        ConnectionHandler.connectUser(connectedUser);

        responder.respond(new SConnected(
                connectedUser.getUuid(),
                connectedUser.getUser().getUsername()
        ));
        connection.close();
    }

    private void disconnectWithReason(SDisconnect.Reason reason, ChannelHandlerContext ctx, Responder responder)
    {
        log.info("Aborted login, reason: {}", reason);
        responder.respond(new SDisconnect(reason));
        ctx.close();
    }
}
