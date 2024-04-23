package me.oneqxz.partyoverlay.server.sctructures.friend;

import lombok.Getter;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@Getter
public class OfflineFriend extends Friend {

    private final long lastSeen;

    public OfflineFriend(int id, String username, long lastSeen) {
        super(id, username);
        this.lastSeen = lastSeen;
    }

    @Override
    public String toString() {
        return "OfflineFriend{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", lastSeen=" + lastSeen +
                '}';
    }
}
