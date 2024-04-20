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

package me.oneqxz.partyoverlay.server.network.protocol.event;

import io.netty.channel.ChannelHandlerContext;
import me.oneqxz.partyoverlay.server.annotations.PacketNeedAuth;
import me.oneqxz.partyoverlay.server.network.ConnectionHandler;
import me.oneqxz.partyoverlay.server.network.protocol.Packet;
import me.oneqxz.partyoverlay.server.network.protocol.io.Responder;
import me.oneqxz.partyoverlay.server.sctructures.ConnectedUser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisteredPacketSubscriber {

    private final Map<Class<? extends Packet>, Set<InvokableEventMethod>> handler = new HashMap<>();

    public RegisteredPacketSubscriber(Object subscriberClass) {
        for (Method method : subscriberClass.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(PacketSubscriber.class)) {
                continue;
            }

            Class<? extends Packet> packetClass = null;
            for (Parameter parameter : method.getParameters()) {
                if (Packet.class.isAssignableFrom(parameter.getType())) {
                    packetClass = (Class<? extends Packet>) parameter.getType();
                    continue;
                }

                if (ChannelHandlerContext.class.isAssignableFrom(parameter.getType())) continue;
                if (Responder.class.isAssignableFrom(parameter.getType())) continue;

                if(method.isAnnotationPresent(PacketNeedAuth.class))
                    if(ConnectedUser.class.isAssignableFrom(parameter.getType())) continue;

                throw new IllegalArgumentException("Invalid parameter for @PacketSubscriber: " + parameter.getType().getSimpleName());
            }

            if (packetClass == null) {
                throw new IllegalArgumentException("Missing packet parameter for @PacketSubscriber");
            }

            handler.computeIfAbsent(packetClass, aClass -> new HashSet<>()).add(new InvokableEventMethod(
                    subscriberClass, method, packetClass
            ));
        }
    }

    public void invoke(Packet rawPacket, ChannelHandlerContext ctx, Responder responder) throws InvocationTargetException, IllegalAccessException {
        Set<InvokableEventMethod> methods = handler.get(rawPacket.getClass());
        if (methods == null) {
            return;
        }

        for (InvokableEventMethod method : methods) {
            if(method.getMethod().isAnnotationPresent(PacketNeedAuth.class))
            {
                ConnectedUser user = ConnectionHandler.getUserByCTX(ctx);
                if(user == null)
                {
                    ctx.close();
                    return;
                }

                method.invoke(rawPacket, ctx, responder, user);
                return;
            }

            method.invoke(rawPacket, ctx, responder, null);
        }
    }

}
