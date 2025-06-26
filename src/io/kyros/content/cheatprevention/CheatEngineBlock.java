package io.kyros.content.cheatprevention;

import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

public class CheatEngineBlock {
	
	public static boolean tradingPostAlert(Player c) {
		if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
			Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.");
			Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.");
			Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.");
			Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Trading post.");
			Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@trading post!");
			Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@trading post!");
			c.getPA().closeAllWindows();
			return true;
		} else {
			Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
			Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
			return false;
		}
	}


		public static boolean BankAlert(Player c) {
			if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Bank.");
				Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@Bank!");
				Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@Bank!");
				c.getPA().closeAllWindows();
				return true;
			} else {
				Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
				Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
				c.getPA().closeAllWindows();
				return false;
			}
			}
		
		public static boolean PresetAlert(Player c) {
			if ((!Boundary.isIn(c, Boundary.EDGE_TRADING_AREA) && !Boundary.isIn(c, Boundary.SKILLING_ISLAND_BANK)) || Boundary.isIn(c, Boundary.OUTLAST_HUT)) {
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.");
				Misc.println("" + c.getDisplayName() + " is trying to use a cheatengine to open the Presets.");
				Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " @blu@is using a cheat engine for the @red@Presets!");
				Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " @blu@is using a cheat engine for the @red@Presets!");
				c.getPA().closeAllWindows();
				return true;
			} else {
				Discord.writeServerSyncMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
				Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " triggered trading post in edge but no jail.");
				c.getPA().closeAllWindows();
				return false;
			}
		}
		public static boolean DonatorBoxAlert(Player c) {
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the donator boxes.");
			Discord.writeServerSyncMessage("[CHEAT ENGINE] "+ c.getDisplayName() +" is using a cheat engine for the @red@donator boxes!");
			Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the @red@donator boxes!");
			c.getPA().closeAllWindows();
			return true;
		}
		public static boolean ExperienceAbuseAlert(Player c) {
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.");
			Misc.println(""+ c.getDisplayName() +" is trying to use a cheatengine to open the lamps.");
			Discord.writeServerSyncMessage("[CHEAT ENGINE] "+ c.getDisplayName() +" is using a cheat engine for the lamps!");
			Discord.writeCheatEngineMessage("[CHEAT ENGINE] " + c.getDisplayName() + " is using a cheat engine for the lamps!");
			c.getPA().closeAllWindows();
			return true;
		}

}