package me.oneqxz.partyoverlay.server.sctructures;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SPartySync;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Getter
@RequiredArgsConstructor
@Setter
@Log4j2
public class Party implements Runnable {

    @NonNull private UUID partyUUID;
    @NonNull private String partyName;
    private List<PartyMember> members = new LinkedList<>();

    private ScheduledFuture<?> scheduledFuture;

    public PartyMember getFirstPartyMember()
    {
        return this.members.stream().findFirst().orElse(null);
    }

    public void removeConnectedUser(ConnectedUser user)
    {
        this.members.removeIf(member -> member.getUser() == user);
    }

    public void removeConnectedMember(PartyMember member)
    {
        this.members.remove(member);
    }

    public PartyMember connectedUserToPartyMember(ConnectedUser user)
    {
        return this.members.stream().filter(member -> member.getUser() == user).findFirst().orElse(null);
    }

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
        PartyMember[] members = this.members.toArray(new PartyMember[0]);
        for(PartyMember member : members)
        {
            if(member.getUser().getCtx().channel().isOpen())
            {
                member.getUser().getCtx().writeAndFlush(
                        new SPartySync(
                                this.partyUUID,
                                this.partyName,
                                members
                        )
                );
            }
        }
    }
}
