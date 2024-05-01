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

    private int hurtTime;
    private String dimension;

    private WrappedItemStack mainHandItem;
    private WrappedItemStack offHandItem;

    private WrappedItemStack helmetItem;
    private WrappedItemStack chestplateItem;
    private WrappedItemStack leggingsItem;
    private WrappedItemStack bootsItem;

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
                0,
                0,
                "null",
                WrappedItemStack.EMPTY(),
                WrappedItemStack.EMPTY(),

                WrappedItemStack.EMPTY(),
                WrappedItemStack.EMPTY(),
                WrappedItemStack.EMPTY(),
                WrappedItemStack.EMPTY()
        );
    }
}

