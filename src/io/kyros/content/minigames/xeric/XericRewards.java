/* Distributes loot after completion of Trials of Xeric.
 * Author @Patrity
 */
package io.kyros.content.minigames.xeric;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.Misc;
/**
 *
 * @author Patrity
 *
 */
public class XericRewards {

	int qty;

	public static void giveReward(int dmg, Player player, int wave) {
		player.totalRaidsFinished++;
		player.sendMessage("You have now completed " + player.totalRaidsFinished + " Trials of Xeric!");
		if (wave >= 20 /*&& Misc.random(0, 100) > 95*/) {
			rareDrop(player);
		} else {
			commonDrop(player);
		}

	}

	public static final int[] rareDropItem = {
			21018,
			21021,
			21024,
			22326,
			22327,
			22328,
			20790,
			20789,
			20788,
			20997,
			22324,
			22323,
			22325,
			20784,
			13576,
			22322,
			21006,
			22477,
			696,
			30015,
			30016,
			30017,
			30019,
			10556,
			10557,
			10558,
			10559,
			20095,
			20098,
			20101,
			20104,
			20107,
			20080,
			20083,
			20086,
			20089,
			20092,
			21003,
			21079,
			21034

	};
	public static final int[] commonDropItem = {
			21880,
			7937,
			537,
			1514,
			452,
			2364,
			23686,
			11944,
			6686,
			384,
			386,
			2996,
			11944,
			384,
			386,
			2996,
			7937,
			537,
			1514,
			452,
			2364,
			23686,
			11944,
			6686,
			384,
			386,
			2996,
			11944,
			384,
			386,
			691,
			1957//onion


	};

	public static void rareDrop(Player player) {
		int rareitem = Misc.random(rareDropItem.length - 1);
		player.getItems().addItemUnderAnyCircumstance(rareDropItem[rareitem], 1);
		PlayerHandler.executeGlobalMessage(player.getDisplayName() + " has received a " + ItemDef.forId(rareDropItem[rareitem]).getName()  + " from Trials of Xeric!");
	}

	public static void commonDrop(Player player) {
		int qty = (200 + Misc.random(180));
		int commonitem = Misc.random(commonDropItem.length - 1);
		int drop = commonDropItem[commonitem];
		if (drop == 1957) {
			qty = 1;
			PlayerHandler.executeGlobalMessage(player.getDisplayName() +" has received THE ONION as a reward from Trials of Xeric");
		} else {
			player.sendMessage("You have received @red@" + ItemDef.forId(drop).getName()
					+ " x" + qty + "@bla@ as a reward from Trials of Xeric!");
		}
		player.getItems().addItemUnderAnyCircumstance(drop, qty);

	}

	public static void calldrops() {

		for (int i : commonDropItem) {
			System.out.println(ItemDef.forId(i).getName());
		}


		for (int i : rareDropItem) {
			System.out.println(ItemDef.forId(i).getName());
		}
	}
}