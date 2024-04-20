package me.oneqxz.partyoverlay.server.network.protocol.packets.c2s;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.AuthCredits;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CLogin extends Packet {

    private String minecraftUsername;
    private AuthCredits credits;

    @Override
    public void read(PacketBuffer buffer) {
        this.minecraftUsername = buffer.readUTF8();
        this.credits = new AuthCredits(
                buffer.readUTF8(),
                buffer.readUTF8()
        );
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUTF8(minecraftUsername);

        buffer.writeUTF8(credits.getUsername());
        buffer.writeUTF8(credits.getPassword());
    }
}
