package me.oneqxz.partyoverlay.server.network.protocol.packets.s2c;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.PartyInvite;
import me.oneqxz.partyoverlay.server.sctructures.PartyMember;

import java.util.UUID;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 21.04.2024
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SPartySync extends Packet {

    private UUID uuid;
    private String name;
    private PartyMember[] members;
    private PartyInvite[] invites;

    @Override
    public void read(PacketBuffer buffer) {
        throw new IllegalStateException("");
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeUUID(this.uuid);
        buffer.writeUTF8(this.name);

        buffer.writeInt(this.members.length);

        for (PartyMember member : this.members) {
            buffer.writeInt(member.getUser().getUser().getId());
            buffer.writeUTF8(member.getUser().getUser().getUsername());
            buffer.writeUTF8(member.getUser().getMinecraftUser().getUsername());
            buffer.writeBoolean(member.isOwner());
            buffer.writeColor(member.getPlayerColor());

            buffer.writeFloat(member.getHealth());
            buffer.writeFloat(member.getMaxHealth());
            buffer.writeFloat(member.getYaw());
            buffer.writeFloat(member.getPitch());

            buffer.writeDouble(member.getPosX());
            buffer.writeDouble(member.getPosY());
            buffer.writeDouble(member.getPosZ());
            buffer.writeByteArray(member.getUser().getMinecraftUser().getSkin());
        }

        buffer.writeInt(this.invites.length);

        for(PartyInvite invite : this.invites) {
            buffer.writeUTF8(invite.getInvited().getUser().getUsername());
        }
    }

}
