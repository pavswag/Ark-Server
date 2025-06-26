package io.kyros.model.entity.player.packets;


import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

/**
 * @author Jason MacKeigan
 * @date Dec 29, 2014, 1:12:35 PM
 */
public class SelectItemOnInterface implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}

		int interfaceId = player.getInStream().readInteger();
		int slot = player.getInStream().readInteger();
		int itemId = player.getInStream().readInteger();
		int itemAmount = player.getInStream().readInteger();
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}

		GameItem item = new GameItem(itemId, itemAmount);
		
		switch (interfaceId) {
			case 73331:
				player.getWheelOfFortune().onFinish(slot);
				break;
		}
	}

}
