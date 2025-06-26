package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.cache.definitions.identifiers.ItemParamIdentifiers;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class ItmParams extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		int id = Integer.parseInt(input);
		ItemDefinition def = Server.definitionRepository.get(ItemDefinition.class, id);
		player.sendMessage("Id=" + id + " | name=" + def.name + " | Params = ");
		if(def.params == null) {
			player.sendMessage("No params found!");
			return;
		}
		def.params.forEach((key, object) -> {
			String obj = object instanceof String ? (String) object : String.valueOf((Integer) object);
			player.sendMessage(ItemParamIdentifiers.getParamName(key) + "->" + obj);
		});
		player.sendMessage(" - ");
	}

}
