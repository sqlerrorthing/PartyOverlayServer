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
 * @since 21.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CPartySync extends Packet {

    private float health, maxHealth, yaw, pitch;
    private double x, y, z;

    @Override
    public void read(PacketBuffer buffer) {
        this.health = buffer.readFloat();
        this.maxHealth = buffer.readFloat();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();

        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeFloat(this.health);
        buffer.writeFloat(this.maxHealth);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);

        buffer.writeDouble(this.x);
        buffer.writeDouble(this.y);
        buffer.writeDouble(this.z);
    }
}
