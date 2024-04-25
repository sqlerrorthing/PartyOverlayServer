package me.oneqxz.partyoverlay.server.network;

/**
 * PartyOverlayServer
 *
 * @author oneqxz
 * @since 23.04.2024
 */

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.util.AttributeKey;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChannelInactiveHandler extends ChannelDuplexHandler {
    private final long timeoutInMillis;

    public ChannelInactiveHandler(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // Канал активен, запускаем таймер для проверки неактивности
        scheduleInactiveCheck(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        // Канал неактивен, отменяем таймер
        cancelInactiveCheck(ctx.channel());
    }

    private void scheduleInactiveCheck(Channel channel) {
        EventLoop eventLoop = channel.eventLoop();
        eventLoop.schedule(() -> {
            if (!channel.isActive()) {
                System.out.println("Канал не активен уже " + timeoutInMillis + " мс.");
                // Дополнительные действия, если канал не активен
                channel.close(); // Закрываем канал
            }
        }, timeoutInMillis, TimeUnit.MILLISECONDS);
    }

    private void cancelInactiveCheck(Channel channel) {
        // Отменяем проверку неактивности, если канал снова стал активным
        // Это может произойти, если канал временно отключился и снова подключился
        channel.eventLoop().execute(() -> {
            // Получаем ScheduledFuture из атрибутов канала
            ScheduledFuture<?> future = (ScheduledFuture<?>) channel.attr(AttributeKey.valueOf("inactiveCheckFuture")).get();
            if (future != null) {
                future.cancel(false); // Отменяем проверку неактивности
            }
        });
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        ScheduledFuture<?> future = ctx.channel().eventLoop().schedule(() -> {}, timeoutInMillis, TimeUnit.MILLISECONDS);
        ctx.channel().attr(AttributeKey.valueOf("inactiveCheckFuture")).set(future);
    }
}
