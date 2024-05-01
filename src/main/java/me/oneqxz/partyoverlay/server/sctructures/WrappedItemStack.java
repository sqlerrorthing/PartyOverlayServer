package me.oneqxz.partyoverlay.server.sctructures;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 30.04.2024
 */
@Getter
@AllArgsConstructor
@ToString
public class WrappedItemStack {

    public static WrappedItemStack EMPTY()
    {
        return new WrappedItemStack("minecraft:air", 0, 1);
    }

    String transitionKey;
    int damage;
    int count;

    public WrappedItemStack read(PacketBuffer buffer)
    {
        this.transitionKey = buffer.readUTF8();
        this.damage = buffer.readInt();
        this.count = buffer.readInt();

        return this;
    }

    public WrappedItemStack write(PacketBuffer buffer)
    {
        buffer.writeUTF8(this.transitionKey);
        buffer.writeInt(this.damage);
        buffer.writeInt(this.count);

        return this;
    }
}
