package me.oneqxz.partyoverlay.server.network.protocol.packets.s2c;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.InviteResult;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 27.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SPartyInviteResult extends Packet {

    private InviteResult result;

    @Override
    public void read(PacketBuffer buffer) {
        this.result = buffer.readEnum(InviteResult.class);
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeEnum(this.result);
    }
}
