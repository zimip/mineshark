package net.zimi.client.mixin;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.zimi.client.Mineshark;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.TimeUnit;

@Mixin(Connection.class)
public class ClientConnectionMixin {

    @Shadow private Channel channel;

    @Inject(method = "channelActive", at = @At("RETURN"))
    private void onChannelActive(ChannelHandlerContext ctx, CallbackInfo ci) {
        if (this.channel != null && this.channel.pipeline().get("mineshark_sniffer") == null) {

            this.channel.pipeline().addBefore("packet_handler", "mineshark_sniffer", new ChannelDuplexHandler() {

                private boolean shouldDelay(Object msg, String name) {
                    if (!(msg instanceof Packet<?>)) {
                        return false;
                    }

                    if (name.contains("Intention") ||
                            name.contains("Hello") ||
                            name.contains("Key") ||
                            name.contains("Login") ||
                            name.contains("Config") ||
                            name.contains("Cookie") ||
                            name.contains("Disconnect")) {
                        return false;
                    }

                    return true;
                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    String name = msg.getClass().getSimpleName();
                    Mineshark.IN_COUNTER.incrementAndGet();

                    if (Mineshark.FILTER_MOVEMENT && (name.contains("Move") || name.contains("Position") || name.contains("Rotation"))) {
                        super.channelRead(ctx, msg);
                        return;
                    }

                    if (Mineshark.PACKET_DELAY_MS > 0 && shouldDelay(msg, name)) {
                        ctx.executor().schedule(() -> {
                            Mineshark.LOGGER.info("[Mineshark IN] [#{} Delayed] <- {}", Mineshark.IN_COUNTER.get(), name);
                            ctx.fireChannelRead(msg);
                        }, Mineshark.PACKET_DELAY_MS, TimeUnit.MILLISECONDS);
                    } else {
                        Mineshark.LOGGER.info("[Mineshark IN] [#{} Instant] <- {}", Mineshark.IN_COUNTER.get(), name);
                        super.channelRead(ctx, msg);
                    }
                }

                @Override
                public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                    String name = msg.getClass().getSimpleName();
                    Mineshark.OUT_COUNTER.incrementAndGet();

                    if (Mineshark.FILTER_MOVEMENT && (name.contains("Move") || name.contains("Position") || name.contains("Rotation"))) {
                        super.write(ctx, msg, promise);
                        return;
                    }

                    if (Mineshark.PACKET_DELAY_MS > 0 && shouldDelay(msg, name)) {
                        ctx.executor().schedule(() -> {
                            Mineshark.LOGGER.info("[Mineshark OUT] [#{} Delayed] -> {}", Mineshark.OUT_COUNTER.get(), name);
                            ctx.write(msg, promise);
                            ctx.flush();
                        }, Mineshark.PACKET_DELAY_MS, TimeUnit.MILLISECONDS);
                    } else {
                        Mineshark.LOGGER.info("[Mineshark OUT] [#{} Instant] -> {}", Mineshark.OUT_COUNTER.get(), name);
                        super.write(ctx, msg, promise);
                    }
                }
            });
        }
    }
}