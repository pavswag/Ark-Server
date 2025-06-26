package io.kyros.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.util.AttributeKey;

public class HAProxyHandler extends ChannelInboundHandlerAdapter {
    public static final AttributeKey<String> CLIENT_IP_KEY = AttributeKey.valueOf("CLIENT_IP");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HAProxyMessage) {
            HAProxyMessage proxyMessage = (HAProxyMessage) msg;
            String clientIp = proxyMessage.sourceAddress();
            ctx.channel().attr(CLIENT_IP_KEY).set(clientIp);
            proxyMessage.release();
        } else {
            super.channelRead(ctx, msg);
        }
    }
}