package me.oneqxz.partyoverlay.server.sctructures.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.oneqxz.partyoverlay.server.database.models.User;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 27.04.2024
 */
@Getter
@AllArgsConstructor

public class FriendRequest {
    private User from;
    private User to;
}
