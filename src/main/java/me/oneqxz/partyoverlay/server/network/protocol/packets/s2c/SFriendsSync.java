package me.oneqxz.partyoverlay.server.network.protocol.packets.s2c;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.buffer.PacketBuffer;
import me.oneqxz.partyoverlay.server.sctructures.ServerData;
import me.oneqxz.partyoverlay.server.sctructures.friend.FriendRequest;
import me.oneqxz.partyoverlay.server.sctructures.friend.OfflineFriend;
import me.oneqxz.partyoverlay.server.sctructures.friend.OnlineFriend;
import me.oneqxz.partyoverlay.server.utils.LinkedSet;

import java.util.Set;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 22.04.2024
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SFriendsSync extends Packet {

    private Set<OnlineFriend> onlineFriends;
    private Set<OfflineFriend> offlineFriends;
    private Set<FriendRequest> friendRequests;

    @Override
    public void read(PacketBuffer buffer) {
        onlineFriends = new LinkedSet<>();
        offlineFriends = new LinkedSet<>();
        friendRequests = new LinkedSet<>();

        int onlineFriendsSize = buffer.readInt();
        for (int i = 0; i < onlineFriendsSize; i++) {
            int id = buffer.readInt();
            String username = buffer.readUTF8();
            byte[] skin = buffer.readByteArray();
            ServerData serverData = null;
            String server = buffer.readUTF8();
            if (!server.equals("[null]")) {
                serverData = ServerData.builder().serverIP(server).build();
            }
            String minecraftUsername = buffer.readUTF8();
            onlineFriends.add(new OnlineFriend(id, username, skin, serverData, minecraftUsername));
        }

        int offlineFriendsSize = buffer.readInt();
        for (int i = 0; i < offlineFriendsSize; i++) {
            int id = buffer.readInt();
            String username = buffer.readUTF8();
            long lastSeen = buffer.readLong();
            offlineFriends.add(new OfflineFriend(id, username, lastSeen));
        }

        int friendRequestsSize = buffer.readInt();
        for (int i = 0; i < friendRequestsSize; i++) {
            int id = buffer.readInt();
            String username = buffer.readUTF8();
            FriendRequest.RequestType requestType = buffer.readEnum(FriendRequest.RequestType.class);
            friendRequests.add(new FriendRequest(id, username, requestType));
        }
    }

    @Override
    public void write(PacketBuffer buffer) {
        buffer.writeInt(this.onlineFriends.size());
        for (OnlineFriend onlineFriend : this.onlineFriends) {
            buffer.writeInt(onlineFriend.getId());
            buffer.writeUTF8(onlineFriend.getUsername());
            buffer.writeByteArray(onlineFriend.getSkin());
            if (onlineFriend.getServerData() != null) {
                buffer.writeUTF8(onlineFriend.getServerData().getServerIP());
            } else {
                buffer.writeUTF8("[null]");
            }
            buffer.writeUTF8(onlineFriend.getMinecraftUsername());
        }

        buffer.writeInt(this.offlineFriends.size());
        for (OfflineFriend offlineFriend : this.offlineFriends) {
            buffer.writeInt(offlineFriend.getId());
            buffer.writeUTF8(offlineFriend.getUsername());
            buffer.writeLong(offlineFriend.getLastSeen());
        }

        buffer.writeInt(this.friendRequests.size());
        for (FriendRequest friendRequest : this.friendRequests) {
            buffer.writeInt(friendRequest.getId());
            buffer.writeUTF8(friendRequest.getUsername());
            buffer.writeEnum(friendRequest.getRequestType());
        }
    }

}