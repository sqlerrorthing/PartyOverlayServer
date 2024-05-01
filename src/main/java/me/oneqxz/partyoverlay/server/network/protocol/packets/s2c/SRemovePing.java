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
 * @since 01.05.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SRemovePing extends Packet {

    private UUID pingUUID;

    @Override
    public void read(PacketBuffer buffer) {
        this.pingUUID = buffer.readUUID();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.pingUUID);
    }
}
