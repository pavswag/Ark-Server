package io.kyros.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

import static io.kyros.net.HAProxyHandler.CLIENT_IP_KEY;

public class ChannelHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger activeConnections = new AtomicInteger();
    private static final Logger logger = LoggerFactory.getLogger(ChannelHandler.class);

    public static int getActiveConnections() {
        return activeConnections.get();
    }

    public static void incrementActiveConnections() {
        activeConnections.getAndIncrement();
    }

    private Session session;
    private String ipAddress;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        if (cause != null) {
//            if (cause.getMessage() != null) {
//                String message = cause.getMessage().toLowerCase();
//                if (message.equals("connection reset by peer") || message.contains("forcibly closed"))
//                    return;
//            }
//
//            if (cause instanceof ReadTimeoutException)
//                return;
//
//            logger.error("Error received in channel", e.getCause());
//        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object e) throws Exception {
        try {
            if (e instanceof HAProxyMessage) {
                HAProxyMessage proxyMessage = (HAProxyMessage) e;
                ipAddress = proxyMessage.sourceAddress();
                ctx.channel().attr(CLIENT_IP_KEY).set(ipAddress);
                proxyMessage.release();
            } else if (e instanceof Player) {
                session.setClient((Player) e);
                PlayerHandler.addLoginQueue(session.getClient());
            } else if (e instanceof Packet) {
                if (session == null) {
                    return;
                }

                Player client = session.getClient();
                if (client != null) {
                    if (client.getPacketsReceived() >= Configuration.MAX_PACKETS_PROCESSED_PER_CYCLE) {
                        int attempted = client.attemptedPackets.incrementAndGet();
                        if (attempted > Configuration.KICK_PLAYER_AFTER_PACKETS_PER_CYCLE) {
                            logger.info("Disconnecting user: " + client + " for sending " + attempted + " packets.");
                            client.getSession().disconnect();
                        }
                        return;
                    }

                    Packet message = (Packet) e;
                    int packetOpcode = message.getOpcode();

                    boolean isPriorityPacket = Packet.isPriorityPacket(packetOpcode);
                    client.queueMessage(message, isPriorityPacket);

                    while (client.getPreviousPackets().size() > 50)
                        client.getPreviousPackets().poll();

                    if (message.getOpcode() != 0)
                        client.getPreviousPackets().add(message.getOpcode());
                }
            }
        } catch (Exception ex) {
            logger.error("Exception while receiving message", ex);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (session == null) {
            session = new Session(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            if (session != null) {
                Player player = session.getClient();
                if (player != null) {
                    player.setDisconnected();
                }
                session = null;
            }
        } catch (Exception ex) {
            logger.error("Exception during xlog", ex);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        activeConnections.decrementAndGet();
    }

    public static String getIP(ChannelHandlerContext ctx) {
        return getIP(ctx.channel());
    }

    public static String getIP(Channel channel) {
        String clientIp = channel.attr(CLIENT_IP_KEY).get();
        if (clientIp != null) {
            return clientIp;
        }
        return ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress();
    }
}
