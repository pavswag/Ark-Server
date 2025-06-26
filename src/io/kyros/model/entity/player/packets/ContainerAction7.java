package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.model.ContainerAction;
import io.kyros.model.ContainerActionType;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.player.ReceivedPacketLog;

public class ContainerAction7 implements PacketType {

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
		player.interruptActions();
		int interfaceId = player.getInStream().readUnsignedWord();
		int itemId = player.getInStream().readSignedWordBigEndianA();
		int itemSlot = player.getInStream().readSignedWordBigEndian();
		Server.getLogging().write(new ReceivedPacketLog(player, packetType, "i=" + interfaceId + "/" + itemSlot + "/" + itemId));

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_7, interfaceId, itemId, itemSlot);

		if (player.debugMessage)
			player.sendMessage("ContainerAction4: interfaceid: "+interfaceId+", removeSlot: "+itemSlot+", removeID: " + itemId);
		
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}

		if (player.getBank().withdraw(interfaceId, itemId, player.getBank().getAllButOne(interfaceId, itemId))) {
			return;
		}
	}

}
