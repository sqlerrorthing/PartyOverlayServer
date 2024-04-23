package me.oneqxz.partyoverlay.server.sctructures.friend;

import lombok.Getter;
import lombok.ToString;
import me.oneqxz.partyoverlay.server.sctructures.ServerData;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@Getter
@ToString
public class OnlineFriend extends Friend {
    private final ServerData serverData;
    private final String minecraftUsername;
    private final byte[] skin;

    public OnlineFriend(int id, String username, byte[] skin, ServerData serverData, String minecraftUsername) {
        super(id, username);
        this.serverData = serverData;
        this.minecraftUsername = minecraftUsername;
        this.skin = skin;
    }

}
