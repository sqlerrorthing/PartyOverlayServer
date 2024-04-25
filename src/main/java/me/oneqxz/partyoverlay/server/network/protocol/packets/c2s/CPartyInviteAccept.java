package me.oneqxz.partyoverlay.server.network.protocol.packets.c2s;

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
 * @since 24.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CPartyInviteAccept extends Packet {

    private UUID partyUUID;

    @Override
    public void read(PacketBuffer buffer) {
        this.partyUUID = buffer.readUUID();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.partyUUID);
    }
}
