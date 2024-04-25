package me.oneqxz.partyoverlay.server.network.protocol.packets.s2c;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.PartyInvite;

import java.util.Set;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 23.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SPartyInviteSync extends Packet {

    private Set<PartyInvite> invites;

    @Override
    public void read(PacketBuffer buffer) {

    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(invites.size());

        for (PartyInvite invite : invites) {
            buffer.writeUTF8(invite.getInviter().getUser().getUsername());
            buffer.writeUUID(invite.getPartyUUID());
        }
    }
}
