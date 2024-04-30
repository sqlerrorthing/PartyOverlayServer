package me.oneqxz.partyoverlay.server.managers;

import com.j256.ormlite.dao.Dao;
import lombok.Getter;
import lombok.SneakyThrows;
import me.oneqxz.partyoverlay.server.database.DatabaseConnection;
import me.oneqxz.partyoverlay.server.database.models.Friendship;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SFriendRequestResult;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;
import me.oneqxz.partyoverlay.server.sctructures.FriendRequestResult;
import me.oneqxz.partyoverlay.server.sctructures.friend.FriendRequest;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 27.04.2024
 */
@Getter
public class FriendRequestManager {

    private static FriendRequestManager INSTANCE;
    private final List<FriendRequest> requests = new LinkedList<>();

    private FriendRequestManager()
    {

    }

    public void proceedRequestAdd(User from, User to, Responder responder)
    {
        if(this.requests.stream().anyMatch(f -> f.getFrom().getId() == from.getId() && f.getTo().getId() == to.getId()))
        {
            responder.respond(new SFriendRequestResult(FriendRequestResult.ALREADY_SEND));
            return;
        }

        if(from.getId() == to.getId())
        {
            responder.respond(new SFriendRequestResult(FriendRequestResult.NOT_FOUND));
            return;
        }

        if(Arrays.stream(from.getFriends()).anyMatch(f -> f.getId() == to.getId()))
        {
            responder.respond(new SFriendRequestResult(FriendRequestResult.ALREADY_FRIENDS));
            return;
        }

        if(this.requests.stream().anyMatch(f -> f.getTo().getId() == to.getId() && f.getFrom().getId() == to.getId()))
        {
            this.proceedFriendAdd(to, from, false);
            responder.respond(new SFriendRequestResult(FriendRequestResult.SEND));
            return;
        }

        this.requests.add(new FriendRequest(from, to));
        responder.respond(new SFriendRequestResult(FriendRequestResult.SEND));
    }

    public static FriendRequestManager getInstance() {
        return INSTANCE == null ? INSTANCE = new FriendRequestManager() : INSTANCE;
    }

    @SneakyThrows
    public void proceedFriendAdd(User from, User to, boolean requestCheck)
    {
        if(requestCheck && this.requests.stream().noneMatch(f -> f.getFrom().getId() == from.getId() && f.getTo().getId() == to.getId()))
            return;

        this.requests.removeIf(f -> f.getFrom().getId() == from.getId() && f.getTo().getId() == to.getId());

        Friendship friendship = new Friendship(from, to);
        Dao<Friendship, Integer> dao = DatabaseConnection.getInstance().getFriendshipsDao();
        dao.createIfNotExists(friendship);

        // SHITCODE ON
        this.refresh(ConnectionHandler.getUserByID(from.getId()));
        this.refresh(ConnectionHandler.getUserByID(to.getId()));
        // SHITCODE OFF
    }

    private void refresh(final ConnectedUser to)
    {
        if (to == null) return;

        // SHITCODE ON
        new Thread(() -> {
            try
            {
                to.getUser().getFriendships1().refreshCollection();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try
            {
                to.getUser().getFriendships2().refreshCollection();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }).start();
        // SHITCODE OFF

    }


    public void proceedFriendReject(User from, User to)
    {
        this.requests.removeIf(f -> f.getFrom().getId() == from.getId() && f.getTo().getId() == to.getId());
        this.requests.removeIf(f -> f.getFrom().getId() == to.getId() && f.getTo().getId() == from.getId());
    }
}
