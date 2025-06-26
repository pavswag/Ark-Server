package io.kyros.model.entity.player.packets.npcoptions;

import io.kyros.Server;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.content.skills.slayer.NewInterface;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCAction;
import io.kyros.model.entity.player.Player;

import static io.kyros.Server.getNpcs;

/*
 * @author Matt
 * Handles all 4th options on non playable characters.
 */

public class NpcOptionFour {

	public static void handleOption(Player player, int npcType) {
		if (Server.getMultiplayerSessionListener().inAnySession(player)) {
			return;
		}
		player.clickNpcType = 0;
		player.clickedNpcIndex = player.npcClickIndex;
		player.npcClickIndex = 0;


        NPC npc = getNpcs().get(player.clickedNpcIndex);
		NpcDef npcDef = NpcDef.forId(npcType);
		NPCAction action = null;
		NPCAction[] actions = npc.actions;
		if(actions != null)
			action = actions[3];
		if(action == null && (actions = npcDef.defaultActions) != null)
			action = actions[3];
		if(action != null) {
			action.handle(player, npc);
			return;
		}
		switch (npcType) {
		case 17: //Rug merchant - Sophanem
			player.startAnimation(2262);
			AgilityHandler.delayFade(player, "NONE", 3285, 2815, 0, "You step on the carpet and take off...", "at last you end up in sophanem.", 3);
			break;

		case 2580:
			player.getPA().startTeleport(3039, 4788, 0, "modern", false);
			player.teleAction = -1;
			break;

		case 402:
		case 401:
		case 405:
		case 6797:
		case 7663:
		case 8761:
		case 5870:
			NewInterface.Open(player);
//			SlayerRewardsInterface.open(player, SlayerRewardsInterfaceData.Tab.TASK);
			//player.getSlayer().handleInterface("buy");
			break;
			
		case 1501:
			player.getShops().openShop(23);
			break;

		case 308:
			player.getDH().sendDialogues(545, npcType);
			break;
		}
	}

}
