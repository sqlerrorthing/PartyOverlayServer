package me.oneqxz.partyoverlay.server.sctructures;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Data
@AllArgsConstructor
public class PartyMember {
    private ConnectedUser user;
    private boolean isOwner;

    private Color playerColor;

    private float health, maxHealth, yaw, pitch;
    private double posX, posY, posZ;

    public static PartyMember fromConnectedUser(ConnectedUser user, boolean isOwner, Color playerColor)
    {
        return new PartyMember(
                user,
                isOwner,
                playerColor,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
    }
}

