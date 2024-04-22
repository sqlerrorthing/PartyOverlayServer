package me.oneqxz.partyoverlay.server.sctructures;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.util.Objects;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class ConnectedMinecraftUser {

    private String username;
    private ServerData serverData;
    private byte[] skin;

    public byte[] getSkin() {
        if(skin != null && skin.length > 0)
            return skin;

        try {
            return Objects.requireNonNull(ConnectedMinecraftUser.class.getResourceAsStream("/assets/skins/grayscale_steve.png")).readAllBytes();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
