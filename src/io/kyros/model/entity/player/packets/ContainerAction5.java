package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.event_manager.EventManager;
import io.kyros.model.ContainerAction;
import io.kyros.model.ContainerActionType;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.script.event.impl.ItemContainerOption;
import io.kyros.util.logging.player.ReceivedPacketLog;

/**
 * Bank X Items
 **/
public class ContainerAction5 implements PacketType {

	public static final int PART1 = 135;

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int xRemoveSlot = c.getInStream().readSignedWordBigEndian();
		int xInterfaceId = c.getInStream().readUnsignedWordA();
		int xRemoveId = c.getInStream().readSignedWordBigEndian();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_5, xInterfaceId, xRemoveId, xRemoveSlot);

		if (packetType == PART1) {
			c.xRemoveSlot = xRemoveSlot;
			c.xInterfaceId = xInterfaceId;
			c.xRemoveId = xRemoveId;
		}

		if (c.getLootingBag().handleClickItem(xRemoveId, -1)) {
			return;
		}

		if (c.viewingRunePouch) {
			c.getRunePouch().setEnterAmountVariables(c.xRemoveId, c.xInterfaceId);
		}
		if (c.debugMessage)
			c.sendMessage("ContainerAction5: interfaceid: "+c.xInterfaceId+", removeSlot: "+c.xRemoveSlot+", removeID: " + c.xRemoveId);
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "ContainerAction5: interfaceid: "+c.xInterfaceId+", removeSlot: "+c.xRemoveSlot+", removeID: " + c.xRemoveId));

		Server.pluginManager.triggerEvent(new ItemContainerOption(c, xInterfaceId, xRemoveSlot, xRemoveId, 5));
/* 		if (c.xInterfaceId == 3823) {
			c.getShops().sellItem(c.xRemoveId, c.xRemoveSlot, 100);// buy 100
			c.xRemoveSlot = 0;
			c.xInterfaceId = 0;
			c.xRemoveId = 0;
			return;
		} */

		/**
		 * Buy 500
		 */
		 if (c.xInterfaceId == 64016) {
			c.buyingX = true;
			 c.getPA().sendEnterAmount(0);
        }


		if(xInterfaceId == 12463) {
			EventManager.add(c, xRemoveId, c.getItems().getItemAmount(xRemoveId));
		}

		 if (c.xInterfaceId == 3823) {
				c.getShops().sellItem(c.xRemoveId, c.xRemoveSlot, 2000000000);//sell all
				c.xRemoveSlot = 0;
				c.xInterfaceId = 0;
				c.xRemoveId = 0;
				return;
		 }

		 if (c.xInterfaceId == 48500) {
			 c.getTradePost().handleInput(c.xInterfaceId, 5, xRemoveId);
			 return;
		 }

		if (c.xInterfaceId == 26022) {
			c.getTradePost().handleInput(c.xInterfaceId, 5, xRemoveSlot);
			return;
		}


		if (packetType == PART1) {
			c.getPA().sendEnterAmount(0);
		}

	}
}
