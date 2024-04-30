/*
 * Copyright (c) 2021, Pierre Maurice Schwang <mail@pschwang.eu> - MIT
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.oneqxz.partyoverlay.server.network.protocol.registry;

import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.exception.PacketRegistrationException;
import me.oneqxz.partyoverlay.server.network.protocol.packets.c2s.*;
import me.oneqxz.partyoverlay.server.network.protocol.packets.s2c.*;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class SimplePacketRegistry implements IPacketRegistry {

    private final Map<Integer, RegisteredPacket> packets = new HashMap<>();


    @Override
    public void registerPacket(int packetId, Packet packet) throws PacketRegistrationException {
        registerPacket(packetId, packet.getClass());
    }

    @Override
    public void registerPacket(int packetId, Class<? extends Packet> packet) throws PacketRegistrationException {
        if (containsPacketId(packetId)) {
            throw new PacketRegistrationException("PacketID is already in use");
        }
        try {
            RegisteredPacket registeredPacket = new RegisteredPacket(packet);
            this.packets.put(packetId, registeredPacket);
        } catch (NoSuchMethodException e) {
            throw new PacketRegistrationException("Failed to register packet", e);
        }
    }

    @Override
    public int getPacketId(Class<? extends Packet> packetClass) {
        for (Map.Entry<Integer, RegisteredPacket> entry : packets.entrySet()) {
            if (entry.getValue().getPacketClass().equals(packetClass)) {
                return entry.getKey();
            }
        }
        return -1;
    }

    @Override
    public <T extends Packet> T constructPacket(int packetId) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return (T) packets.get(packetId).getConstructor().newInstance();
    }

    @Override
    public boolean containsPacketId(int id) {
        return packets.containsKey(id);
    }


    public void registerPackets() throws PacketRegistrationException {
        this.registerPacket(0, SRequireLogin.class);
        this.registerPacket(1, CLogin.class);
        this.registerPacket(2, SDisconnect.class);
        this.registerPacket(3, SConnected.class);
        this.registerPacket(4, CMinecraftUsernameChanged.class);

        this.registerPacket(5, CStartPlaying.class);
        this.registerPacket(6, CStopPlaying.class);

        this.registerPacket(7, CPartyCreate.class);
        this.registerPacket(8, SPartySync.class);
        this.registerPacket(9, CPartySync.class);

        this.registerPacket(10, CSkinSync.class);

        this.registerPacket(11, SFriendsSync.class);
        this.registerPacket(12, CFriendRemove.class);
        this.registerPacket(13, CFriendPartyInvite.class);

        this.registerPacket(14, CPartyLeave.class);
        this.registerPacket(15, SPartyInviteSync.class);

        this.registerPacket(16, CPartyInviteAccept.class);
        this.registerPacket(17, CPartyInviteReject.class);

        this.registerPacket(18, SFriendJoin.class);
        this.registerPacket(19, SFriendLeave.class);

        this.registerPacket(20, SMemberPartyJoin.class);
        this.registerPacket(21, SMemberPartyLeave.class);

        this.registerPacket(22, SPartyInviteResult.class);
        this.registerPacket(23, SNewInvite.class);

        this.registerPacket(24, CFriendRequest.class);
        this.registerPacket(25, SFriendRequestResult.class);

        this.registerPacket(26, CAcceptFriendRequest.class);
        this.registerPacket(27, CRejectFriendRequest.class);
    }

}
