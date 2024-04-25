package me.oneqxz.partyoverlay.server.sctructures;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.oneqxz.partyoverlay.server.managers.PartyInviteManager;
import me.oneqxz.partyoverlay.server.managers.PartyManager;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.SPartyInviteSync;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 23.04.2024
 */
@RequiredArgsConstructor
@Getter
public class PartyInvite implements Runnable {
    @NonNull private ConnectedUser inviter;
    @NonNull private ConnectedUser invited;
    @NonNull private UUID partyUUID;

    private ScheduledFuture<?> scheduledFuture;

    public void start(ScheduledExecutorService executor, long initialDelay, long period, TimeUnit unit) {
        this.scheduledFuture = executor.scheduleAtFixedRate(this, initialDelay, period, unit);
    }

    public void stop() {
        if (this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
            this.scheduledFuture.cancel(true);
        }
    }

    @Override
    public void run() {
        if(!(invited.getCtx().channel().isOpen() || inviter.getCtx().channel().isOpen()) ||
                (invited.getCtx().isRemoved()) || inviter.getCtx().isRemoved()) {
            remove();
            return;
        }

        Party invitedParty = PartyManager.getInstance().getPartyByUUID(this.partyUUID);
        if(invitedParty == null)
        {
            remove();
            return;
        }

        if(invitedParty.getPartyOwner().getUser().getUser().getId() != inviter.getUser().getId())
        {
            remove();
        }

        invited.getCtx().channel().writeAndFlush(new SPartyInviteSync(
                PartyInviteManager.getInstance().getPartyInvitesForConnectedUser(invited)
        ));
    }

    private void remove()
    {
        PartyInviteManager.getInstance().proceedPartyInviteRemove(this);
    }
}
