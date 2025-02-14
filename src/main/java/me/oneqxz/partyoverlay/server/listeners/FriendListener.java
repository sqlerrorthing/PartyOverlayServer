package me.oneqxz.partyoverlay.server.listeners;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.database.DatabaseConnection;
import me.oneqxz.partyoverlay.server.database.models.Friendship;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.managers.FriendRequestManager;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.event.PacketSubscriber;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CAcceptFriendRequest;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CFriendRemove;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CFriendRequest;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.CRejectFriendRequest;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SFriendRequestResult;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.sctructures.FriendRequestResult;

import java.sql.SQLException;
import java.util.List;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
public class FriendListener {

    @SneakyThrows
    @PacketSubscriber
    @PacketNeedAuth
    public void onFriendRemove(CFriendRemove packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        Dao<Friendship, Integer> friendshipDao = DatabaseConnection.getInstance().getFriendshipsDao();

        QueryBuilder<Friendship, Integer> queryBuilder = friendshipDao.queryBuilder();
        Where<Friendship, Integer> where = queryBuilder.where();
        where.or(
                where.and(
                        where.eq("user1_id", user.getUser().getId()),
                        where.eq("user2_id", packet.getFriendID())
                ),
                where.and(
                        where.eq("user1_id", packet.getFriendID()),
                        where.eq("user2_id", user.getUser().getId())
                )
        );

        PreparedQuery<Friendship> preparedQuery = queryBuilder.prepare();
        List<Friendship> friendships = friendshipDao.query(preparedQuery);

        for (Friendship friendship : friendships) {
            friendshipDao.delete(friendship);
            ConnectionHandler.getConnectedUsers().forEach(u -> {
                if(u.getUser().getId() == user.getUser().getId() || u.getUser().getId() == packet.getFriendID()) {
                    new Thread(() -> {
                        try
                        {
                            u.getUser().getFriendships1().refreshCollection();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }).start();
                    new Thread(() -> {
                        try
                        {
                            u.getUser().getFriendships2().refreshCollection();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
        }
    }

    @SneakyThrows
    @PacketNeedAuth
    @PacketSubscriber
    public void onFriendRequest(CFriendRequest packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        User to = getUser(packet.getUsername());

        if(to == null)
        {
            responder.respond(new SFriendRequestResult(FriendRequestResult.NOT_FOUND));
            return;
        }

        FriendRequestManager.getInstance().proceedRequestAdd(user.getUser(), to, responder);
    }

    @SneakyThrows
    @PacketNeedAuth
    @PacketSubscriber
    public void onFriendRequestAccept(CAcceptFriendRequest packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        User from = getUser(packet.getUsername());
        if (from == null) return;

        FriendRequestManager.getInstance().proceedFriendAdd(from, user.getUser(), true);
    }

    @SneakyThrows
    @PacketNeedAuth
    @PacketSubscriber
    public void onFriendRequestReject(CRejectFriendRequest packet, ChannelHandlerContext ctx, Responder responder, ConnectedUser user)
    {
        User from = getUser(packet.getUsername());
        if (from == null) return;

        FriendRequestManager.getInstance().proceedFriendReject(from, user.getUser());
    }

    private User getUser(String username) {
        ConnectedUser connectedUser = ConnectionHandler.getUserByUsername(username);
        User to;
        if(connectedUser != null)
            to = connectedUser.getUser();
        else
            to = getUserByUsername(username);

        return to;
    }

    private User getUserByUsername(String username)
    {
        Dao<User, Integer> userDao = DatabaseConnection.getInstance().getUsersDao();
        QueryBuilder<User, Integer> queryBuilder = userDao.queryBuilder();
        Where<User, Integer> where = queryBuilder.where();
        try
        {
            where.eq("username", username);
            return queryBuilder.queryForFirst();
        }
        catch (SQLException e)
        {
            return null;
        }
    }
}
