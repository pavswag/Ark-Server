package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.cache.definitions.AnimationDefinition;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class ItmAnim extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		int id = Integer.parseInt(input);
		ItemDefinition def = Server.definitionRepository.get(ItemDefinition.class, id);
		player.sendMessage("Finding anims that use item [Id=" + id + " | name=" + def.name + "]");
		for(int i = 0; i < 25_000; i++) {
			AnimationDefinition animationDefinition = Server.definitionRepository.get(AnimationDefinition.class, i);
			if(animationDefinition == null)
				continue;
			if(animationDefinition.leftHandItem == id || animationDefinition.rightHandItem == id) {
				player.sendMessage("ANim[" + i + "]");
			}
		}
		player.sendMessage(" - ");
	}

}
