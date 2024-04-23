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
 * @since 22.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CFriendRemove extends Packet {

    private int friendID;

    @Override
    public void read(PacketBuffer buffer) {
        this.friendID = buffer.readInt();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(friendID);
    }
}
