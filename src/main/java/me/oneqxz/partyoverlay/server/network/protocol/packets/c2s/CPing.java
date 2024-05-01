package me.oneqxz.partyoverlay.server.network.protocol.packets.c2s;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 01.05.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CPing extends Packet {

    private double x, y, z;

    @Override
    public void read(PacketBuffer buffer) {
        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }
}
