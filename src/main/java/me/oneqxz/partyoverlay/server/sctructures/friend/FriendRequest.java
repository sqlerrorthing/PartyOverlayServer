package me.oneqxz.partyoverlay.server.sctructures.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@AllArgsConstructor
@Getter
public class FriendRequest {

    private int id;
    private String username;
    private RequestType requestType;

    public enum RequestType {
        OUTGOING,
        INCOMING;
    }

}
