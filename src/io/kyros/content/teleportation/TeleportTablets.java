package io.kyros.content.teleportation;

import io.kyros.Configuration;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

/**
 * 
 * @author Mack
 *
 */
public class TeleportTablets {

	public enum TabType {
		//		Standard
		HOME(8013, Configuration.RESPAWN_X, Configuration.RESPAWN_Y),
		VARROCK(8007, Configuration.VARROCK_X, Configuration.VARROCK_Y),
		LUMBRIDGE(8008, Configuration.LUMBY_X, Configuration.LUMBY_Y),
		FALADOR(8009, Configuration.FALADOR_X, Configuration.FALADOR_Y),
		CAMELOT(8010, Configuration.CAMELOT_X, Configuration.CAMELOT_Y),
		ARDOUGNE(8011, Configuration.ARDOUGNE_X, Configuration.ARDOUGNE_Y),


		//		Redirect Tablets
		RIMMINGTON(11741, 2956, 3217),
		TAVERLEY(11742, 2896, 3456),
		POLLNIVENEACH(11743, 3351, 2960),
		RELLEKKA(11744, 2643, 3676),
		BRIMHAVEN(11745, 2794, 3178),
		YANILLE(11746, 2606, 3098),
		TROLLHEIM(11747, Configuration.TROLLHEIM_X, Configuration.TROLLHEIM_Y),


		//		Arceuus Tablets
		ARCEUUS_LIBRARY(19613, 1646, 3806),
		DRAYNOR_MANNOR(19615, 3109, 3346),
		SALVE_GRAVEYARD(19619, 3434, 3461),
		FENKENSTRAIN_CASTLE(19621, 3548, 3529),
		WEST_ARDOUGNE(19623, 2538, 3305),
		HARMONY_ISLAND(19625, 3794, 2851),
		CEMETERY(19627, 2976, 3751),
		BARROWS(19629, 3565, 3306),
		APE_ATTOL(19631, 2757, 2781),


		//		Ancient Tablets
		PADDEWWA(12781, Configuration.PADDEWWA_X, Configuration.PADDEWWA_Y),
		SENNTISTEN(12782, Configuration.SENNTISTEN_X, Configuration.SENNTISTEN_Y),
		KHARYRLL(12779, Configuration.KHARYRLL_X, Configuration.KHARYRLL_Y),
		LASSAR(12780, Configuration.LASSAR_X, Configuration.LASSAR_Y),
		DAREEYAK(12777, Configuration.DAREEYAK_X, Configuration.DAREEYAK_Y),
		CARRALLANGER(12776, Configuration.CARRALLANGAR_X, Configuration.CARRALLANGAR_Y),
		ANNAKARL(12775, Configuration.ANNAKARL_X, Configuration.ANNAKARL_Y),
		GHORROCK(12778, Configuration.GHORROCK_X, Configuration.GHORROCK_Y),


//		Lunar
		MOONCLAN(24949, 2113,3915),
		OURANIA(24951, 3015, 5622),
		WATERBIRTH_ISLAND(24953,2527, 3740),
		BARBARIAN(24955, 2519, 3571),
		KHAZARD(24957, 2660, 3158),
		FISHING_GUILD(24959, 2635, 3425),
		CATHERBY(24961, 2804, 3433),
		ICE_PLATEU(24963, 2916, 3921),



//		Misc
		WILDY_RESOURCE(12409, 3184, 3945),
		PIRATE_HUT(12407, 3045, 3956),
		MAGE_BANK(12410, 2538, 4716),
		CALLISTO(12408, 3293, 3847),
		KBD_LAIR(12411, 2271, 4681);




		private final int tab;
		private final int x;
		private final int y;

		public int getTabId() {
			return tab;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		TabType(int tab, int x, int y) {
			this.tab = tab;
			this.x = x;
			this.y = y;
		}
		}

	/**
	 * Operates the teleport tab
	 * 
	 * @param player
	 * @param item
	 */
	public static void operate(final Player player, int item) {
		for (TabType type : TabType.values()) {
			if (type.getTabId() == item) {
				if (System.currentTimeMillis() - player.lastTeleport < 3500)
					return;	
				if (!player.getPA().canTeleport("modern")) {
					return;
				}
				if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
					player.sendMessage("Please leave the outlast hut area to teleport.");
					return ;
				}
				if (Boundary.isIn(player, Boundary.RAIDS_LOBBY) || Boundary.isIn(player, Boundary.RAIDS)) {
					player.sendMessage("Please leave the raids to teleport.");
					return ;
				}
				player.teleporting = true;
				player.getItems().deleteItem(type.getTabId(), 1);
				player.lastTeleport = System.currentTimeMillis();
				player.startAnimation(4731);
				player.gfx0(678);
				final int x = type.getX();
				final int y = type.getY();
				CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

					@Override
					public void execute(CycleEventContainer container) {
						player.setTeleportToX(x);
						player.setTeleportToY(y);
						player.heightLevel = 0;
						player.teleporting = false;
						player.gfx0(-1);
						player.startAnimation(65535);
						container.stop();
					}

				}, 3);
			}
		}
	}

}
