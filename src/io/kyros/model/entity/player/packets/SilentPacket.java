package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.player.ReceivedPacketLog;

/**
 * Slient Packet
 **/
public class SilentPacket implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "data type: " + packetType + ", data length: " + packetSize));
	}
}
