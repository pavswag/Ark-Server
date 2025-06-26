package io.kyros.net;

import io.kyros.Configuration;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.kyros.net.login.RS2Encoder;
import io.kyros.net.login.RS2LoginProtocol;

public class PipelineFactory extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		if (Configuration.PROXY_SERVER) {
			pipeline.addFirst("haproxyDecoder", new HAProxyMessageDecoder());
			/*pipeline.addLast("haproxyHandler", new HAProxyHandler());*/
		}
		pipeline.addLast("filter", new LoginLimitFilter());
		pipeline.addLast("channel_traffic", new ChannelTrafficShapingHandler(0, 1024 * 5, 1000));
		pipeline.addLast("timeout", new IdleStateHandler(10, 0, 0));
		pipeline.addLast("encoder", new RS2Encoder());
		pipeline.addLast("decoder", new RS2LoginProtocol());
		pipeline.addLast("handler", new ChannelHandler());
	}
}
