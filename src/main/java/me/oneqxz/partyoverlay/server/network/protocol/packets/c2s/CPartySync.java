package me.oneqxz.partyoverlay.server.network.protocol.packets.c2s;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.WrappedItemStack;

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

    private int hurtTime;

    private WrappedItemStack mainHandItem;
    private WrappedItemStack offHandItem;

    private WrappedItemStack helmetItem;
    private WrappedItemStack chestplateItem;
    private WrappedItemStack leggingsItem;
    private WrappedItemStack bootsItem;

    @Override
    public void read(PacketBuffer buffer) {
        this.health = buffer.readFloat();
        this.maxHealth = buffer.readFloat();
        this.yaw = buffer.readFloat();
        this.pitch = buffer.readFloat();

        this.x = buffer.readDouble();
        this.y = buffer.readDouble();
        this.z = buffer.readDouble();

        this.hurtTime = buffer.readInt();

        this.mainHandItem = WrappedItemStack.EMPTY().read(buffer);
        this.offHandItem = WrappedItemStack.EMPTY().read(buffer);

        this.helmetItem = WrappedItemStack.EMPTY().read(buffer);
        this.chestplateItem = WrappedItemStack.EMPTY().read(buffer);
        this.leggingsItem = WrappedItemStack.EMPTY().read(buffer);
        this.bootsItem = WrappedItemStack.EMPTY().read(buffer);
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

        buffer.writeInt(this.hurtTime);

        this.mainHandItem.write(buffer);
        this.offHandItem.write(buffer);

        this.helmetItem.write(buffer);
        this.chestplateItem.write(buffer);
        this.leggingsItem.write(buffer);
        this.bootsItem.write(buffer);
    }
}
