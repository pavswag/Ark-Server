package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.help.HelpDatabase;
import io.kyros.content.help.HelpRequest;
import io.kyros.content.sms.SmsManager;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.bank.BankPin;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemStatRequest implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		int id = player.inStream.readInteger();
		player.requestedItemStats.add(new Player.ItemStatRequest(id, ItemStats.forId(id)));
	}

}
