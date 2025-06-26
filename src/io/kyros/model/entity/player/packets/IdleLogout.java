package io.kyros.model.entity.player.packets;

import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

public class IdleLogout implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (!c.isIdle) {
			if (c.debugMessage) {
				c.sendMessage("You are now in idle mode.");
			}
			c.isIdle = true;
			if (c.getRights().isOrInherits(Right.HELPER) && c.getRights().isNot(Right.GAME_DEVELOPER) && c.getRights().isNot(Right.STAFF_MANAGER)) {
				if (c.clan != null) {
					c.clan.removeMember(c);
					c.setLastClanChat("");
				}
			}
		}
	}
}