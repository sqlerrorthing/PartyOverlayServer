package me.oneqxz.partyoverlay.server.sctructures;

import io.netty.channel.ChannelHandlerContext;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.database.models.User;
import me.oneqxz.partyoverlay.server.managers.PartyManager;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SFriendsSync;
import me.oneqxz.partyoverlay.server.sctructures.friend.OfflineFriend;
import me.oneqxz.partyoverlay.server.sctructures.friend.OnlineFriend;
import me.oneqxz.partyoverlay.server.utils.LinkedSet;
import me.oneqxz.partyoverlay.server.utils.TimeUtils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@AllArgsConstructor
@Builder
@Data
@Log4j2
@EqualsAndHashCode
public class ConnectedUser {
    private UUID uuid;
    private ChannelHandlerContext ctx;
    private ConnectedMinecraftUser minecraftUser;
    private User user;

    @Getter(AccessLevel.PRIVATE) private SyncFriends syncFriends;

    public void start(ScheduledExecutorService executor)
    {
        this.syncFriends = new SyncFriends(this, executor, 5000, 1000, TimeUnit.MILLISECONDS);
    }

    public void stop()
    {
        this.syncFriends.stop();
    }

    public boolean isOnParty()
    {
        return PartyManager.getInstance().isOnParty(this);
    }

    public Party getUserParty()
    {
        return PartyManager.getInstance().getPartyByConnectedUser(this);
    }

    private static class SyncFriends implements Runnable {

        private final ConnectedUser reference;
        private final ScheduledFuture<?> scheduledFuture;

        public SyncFriends(ConnectedUser reference, ScheduledExecutorService executor, long initialDelay, long period, TimeUnit unit) {
            this.reference = reference;
            this.scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, period, unit);
        }

        public void stop() {
            if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
                this.scheduledFuture.cancel(true);
            }
        }

        @Override
        public void run() {
            Set<OnlineFriend> onlineFriends = new LinkedSet<>();
            Set<OfflineFriend> offlineFriends = new LinkedSet<>();

            for(User friend : reference.getUser().getFriends())
            {
                ConnectedUser connectedUser = ConnectionHandler.getUserByID(friend.getId());
                if(connectedUser == null)
                {
                    offlineFriends.add(new OfflineFriend(
                            friend.getId(),
                            friend.getUsername(),
                            TimeUtils.getUTCMillis() - friend.getLastOnline()
                    ));
                }
                else
                {
                    onlineFriends.add(new OnlineFriend(
                            friend.getId(),
                            friend.getUsername(),
                            connectedUser.getMinecraftUser().getSkin(),
                            connectedUser.getMinecraftUser().getServerData(),
                            connectedUser.getMinecraftUser().getUsername()
                    ));
                }
            }

            SFriendsSync friendsSync = new SFriendsSync(
                    onlineFriends,
                    offlineFriends,
                    new LinkedSet<>()
            );
            reference.ctx.writeAndFlush(friendsSync);
        }
    }
}
