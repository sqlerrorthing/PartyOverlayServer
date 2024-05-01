package me.oneqxz.partyoverlay.server.network.protocol.packets.s2c;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;

import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 17.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SConnected extends Packet {

    private UUID sessionUUID;
    private String username;
    private int id;

    @Override
    public void read(PacketBuffer buffer) {
        this.sessionUUID = buffer.readUUID();
        this.username = buffer.readUTF8();
        this.id = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.sessionUUID);
        buffer.writeUTF8(this.username);
        buffer.writeInt(this.id);
    }
}

