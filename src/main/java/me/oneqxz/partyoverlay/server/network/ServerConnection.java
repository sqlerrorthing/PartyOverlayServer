package me.oneqxz.partyoverlay.server.network;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.util.concurrent.DefaultEventExecutor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.oneqxz.partyoverlay.server.network.protocol.event.EventRegistry;
import me.oneqxz.partyoverlay.server.network.protocol.exception.PacketRegistrationException;
import me.oneqxz.partyoverlay.server.network.protocol.handler.PacketChannelInboundHandler;
import me.oneqxz.partyoverlay.server.network.protocol.handler.PacketDecoder;
import me.oneqxz.partyoverlay.server.network.protocol.handler.PacketEncoder;
import me.oneqxz.partyoverlay.server.network.protocol.listeners.GlobalPacketListener;
import me.oneqxz.partyoverlay.server.network.protocol.registry.SimplePacketRegistry;


@Log4j2
public class ServerConnection {

    private static ServerConnection INSTANCE;

    private SimplePacketRegistry packetRegistry;
    private EventRegistry eventRegistry;
    @Getter
    private ChannelFuture connection;

    public void init()
    {
        log.info("Starting server....");
        new Thread(() -> {
            eventRegistry = new EventRegistry();
            eventRegistry.registerEvents(new GlobalPacketListener());

            EventLoopGroup bossGroup = new NioEventLoopGroup();
            ChannelGroup channelGroup = new DefaultChannelGroup(new DefaultEventExecutor());
            EventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                packetRegistry = new SimplePacketRegistry();
                packetRegistry.registerPackets();
            } catch (PacketRegistrationException e) {
                e.printStackTrace();
                System.exit(0);
            }

            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap
                        .group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<Channel>() {
                            @Override
                            protected void initChannel(Channel ch) {
                                ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                                ch.pipeline().addLast(new PacketDecoder(packetRegistry));

                                ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
                                ch.pipeline().addLast(new PacketEncoder(packetRegistry));

                                ch.pipeline().addLast(new ConnectionHandler());
                                ch.pipeline().addLast(new PacketChannelInboundHandler(eventRegistry));
                            }
                        })
                        .option(ChannelOption.SO_BACKLOG, 128)
                        .childOption(ChannelOption.AUTO_READ, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true)
                        .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

                ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
                connection = channelFuture;
                log.info("Server started!");
                channelFuture.channel().closeFuture().sync();
            } catch (ChannelException | InterruptedException e) {
                e.printStackTrace();
                System.exit(0);
            } finally {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }
        }, "Netty server").start();
    }

    public static ServerConnection getInstance()
    {
        return INSTANCE == null ? INSTANCE = new ServerConnection() : INSTANCE;
    }
}
