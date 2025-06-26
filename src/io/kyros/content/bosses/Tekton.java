package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class Tekton {

	public static void tektonSpecial(Player player) {
		NPC TEKTON = NPCHandler.getNpc(7544);

		if (TEKTON.isDead()) {
			return;
		}

		Server.getNpcs().get(TEKTON.getIndex()).forceChat("RAAAAAAAA!");
		TEKTON.underAttackBy = -1;
		TEKTON.underAttack = false;

		if (Misc.isLucky(5)) {
			DonorBoss3.burnGFX(player, TEKTON);
		}
	}
}
