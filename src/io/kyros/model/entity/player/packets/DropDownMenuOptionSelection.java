package io.kyros.model.entity.player.packets;

import io.kyros.content.event_manager.EventManager;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;

public class DropDownMenuOptionSelection implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int widgetId = player.inStream.readInteger();
		int optionSelected = player.inStream.readUnsignedWord();
		if (player.debugMessage) {
			player.sendMessage("DropDownMenuOptionSelection = widget ID [" + widgetId + "] / option selected [" + optionSelected + "]");
		}

		if(player.staffPanel.handleDropdownMenuOption(widgetId, optionSelected))
			return;
		if(EventManager.handleDropDown(player, widgetId, optionSelected))
			return;
	}

}
