package io.kyros.model.entity.player.packets.action;

import io.kyros.content.keyboard_actions.KeyboardAction;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;

/**
 * @author Leviticus | www.rune-server.ee/members/leviticus
 * @version 1.0
 */
public class KeyboardShortcutAction implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		int action = player.getInStream().readUnsignedByte();
		player.debug(String.format("KeyboardShortcutAction action=%d", action));

		for(KeyboardAction keyboardAction : KeyboardAction.values()) {
			if (action == keyboardAction.getAction()) {
				keyboardAction.execute(player);
				break;
			}
		}
	}
}