package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;

/**
 * Spawn a specific Npc.
 * 
 * @author Emiel
 *
 */
public class Npc extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		int newNPC = Integer.parseInt(input);
		if (newNPC > 0) {
//			NPCHandler.despawn(newNPC, c.heightLevel);
			NPC npc = NPCSpawning.spawnNpc(newNPC, c.absX, c.absY, c.heightLevel, 1, 10);
			npc.getBehaviour().setRespawn(true);
/*			NPCHandler.newNPC(spawn.id, spawn.position.getX(), spawn.position.getY(), spawn.position.getHeight(), walkingTypeOrdinal, NpcMaxHit.getMaxHit(spawn.id));
			NPCSpawning.spawnNpc(c, newNPC, c.absX, c.absY, c.heightLevel, 0, 7, false, false);*/
			c.sendMessage("You spawn npc " + NpcDef.forId(newNPC).getName() + ", "+ newNPC);
		} else {
			c.sendMessage("No such NPC.");
		}
	}
}
