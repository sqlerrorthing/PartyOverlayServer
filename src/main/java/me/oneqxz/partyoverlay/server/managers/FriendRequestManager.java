package me.oneqxz.partyoverlay.server.managers;

import lombok.Getter;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SFriendRequestResult;
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

        if(Arrays.stream(from.getFriends()).anyMatch(f -> f.getId() == to.getId()))
        {
            responder.respond(new SFriendRequestResult(FriendRequestResult.ALREADY_FRIENDS));
            return;
        }

        this.requests.add(new FriendRequest(from, to));
        responder.respond(new SFriendRequestResult(FriendRequestResult.SEND));
    }

    public static FriendRequestManager getInstance() {
        return INSTANCE == null ? INSTANCE = new FriendRequestManager() : INSTANCE;
    }

}
