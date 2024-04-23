package me.oneqxz.partyoverlay.server.sctructures;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.database.models.User;
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
public class ConnectedUser implements Runnable {
    private UUID uuid;
    private ChannelHandlerContext ctx;
    private ConnectedMinecraftUser minecraftUser;
    private User user;

    private ScheduledFuture<?> scheduledFuture;

    public void start(ScheduledExecutorService executor, long initialDelay, long period, TimeUnit unit) {
        scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, period, unit);
    }

    public void stop() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
    }

    @Override
    public void run() {
        Set<OnlineFriend> onlineFriends = new LinkedSet<>();
        Set<OfflineFriend> offlineFriends = new LinkedSet<>();

        for(User friend : user.getFriends())
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
        log.info("Sync friends to {}, {}", this.user.getUsername(), friendsSync);
        ctx.writeAndFlush(friendsSync);
    }
}
