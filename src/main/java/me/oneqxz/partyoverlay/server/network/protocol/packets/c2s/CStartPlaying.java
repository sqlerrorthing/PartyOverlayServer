package me.oneqxz.partyoverlay.server.network.protocol.packets.c2s;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.ServerData;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CStartPlaying extends Packet {

    private ServerData serverData;

    @Override
    public void read(PacketBuffer buffer) {
        this.serverData = ServerData.builder()
                .serverIP(buffer.readUTF8())
                .build();
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUTF8(this.serverData.getServerIP());
    }
}
