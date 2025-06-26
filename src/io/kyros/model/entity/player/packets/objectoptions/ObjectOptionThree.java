package io.kyros.model.entity.player.packets.objectoptions;

import io.kyros.Server;
import io.kyros.content.bosses.godwars.impl.ArmadylInstance;
import io.kyros.content.bosses.godwars.impl.BandosInstance;
import io.kyros.content.bosses.godwars.impl.SaradominInstance;
import io.kyros.content.bosses.godwars.impl.ZamorakInstance;
import io.kyros.content.bosses.wintertodt.WintertodtActions;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.dialogue.impl.OutlastLeaderboard;
import io.kyros.content.minigames.inferno.Inferno;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.objects.ObjectAction;

/*
 * @author Matt
 * Handles all 3rd options for objects.
 */

public class ObjectOptionThree {

	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;
		// c.sendMessage("Object type: " + objectType);

		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);

		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 3:  "+objectType+"");

		ObjectDef def = ObjectDef.getObjectDef(objectType);
		ObjectAction action = null;
		ObjectAction[] actions = def.defaultActions;
		if(actions != null)
			action = actions[2];
		if(action == null && (actions = def.defaultActions) != null)
			action = actions[2];
		if(action != null) {
			action.handle(c, object);
			return;
		}

		if (OutlastLeaderboard.handleInteraction(c, objectType, 3))
			return;
		if (WintertodtActions.handleObjects(object, c, 3))
			return;
		//if (Halloween.handleCauldron(c, 3, objectType)) {
		//	return;
		//}

		switch (objectType) {
			case 26505:
				if (c.absX == 2925 &&c.absY == 5333) {
					ZamorakInstance instance = new ZamorakInstance(c, Boundary.ZAMORAK_GODWARS);
					ZamorakInstance.enter(c, instance);
				}
				break;
			case 14747:
				if (c.getPosition().inWild() && c.getHeight() == 1) {
					c.getPA().movePlayer(c.getX(), c.getY(), 0);
					return;
				}
				if (c.getPosition().inWild() && c.getHeight() == 2) {
					c.getPA().movePlayer(c.getX(), c.getY(), 1);
				}
				break;

			case 14736:
				c.getPA().movePlayer(c.getX(), c.getY(), c.getHeight() -1);
				break;
			case 26504:
				if (c.absX == 2909 && c.absY == 5265) {
					SaradominInstance instance = new SaradominInstance(c, Boundary.SARADOMIN_GODWARS);
					SaradominInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.SARADOMIN);
				}
				break;

			case 30386:
				c.start(new DialogueBuilder(c).option("Welcome warrior, what would you like to do?", new DialogueOption("Enter The Inferno.", p -> Inferno.startInferno(p, Inferno.getDefaultWave())),
						new DialogueOption("Gamble Infernal Cape", Inferno::gamble)));
				break;
			case 26502:
				if (c.absX == 2839 && c.absY == 5294) {
					ArmadylInstance instance = new ArmadylInstance(c, Boundary.ARMADYL_GODWARS);
					ArmadylInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.ARMADYL);
				}
				break;
			case 26503:
				if (c.absX == 2862 && c.absY == 5354) {
					BandosInstance instance = new BandosInstance(c, Boundary.BANDOS_GODWARS);
					BandosInstance.enter(c, instance);
//					c.getGodwars().enterBossRoom(God.BANDOS);
				}
				break;
			case 31858:
			case 29150:
				c.sendMessage("You switch to the lunar spellbook.");
				c.setSidebarInterface(6, 29999);
				c.playerMagicBook = 2;
				break;

			case 29777:
			case 29734:
			case 10777:
			case 29879:
				c.objectDistance = 4;

				break;
			case 2884:
			case 16684:
			case 16683:
				if (c.absY == 3494 || c.absY == 3495 || c.absY == 3496) {
					AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 2);
				}
				break;
/*		case 29333:
			if (c.getMode().isIronmanType()) {
				c.sendMessage("@red@You are not permitted to make use of this.");			}
			Listing.collectMoney(c);
			
			break;
		case 6448:
			if (c.getMode().isIronmanType()) {
				Listing.openPost(c, false);	
			}
			c.sendMessage("@red@You cannot enter the trading post on this mode.");
			break;*/
			case 8356://streexerics
				c.getPA().movePlayer(1311, 3614, 0);
				break;
			case 7811:
				if (!c.getPosition().inClanWarsSafe()) {
					return;
				}
				c.getDH().sendDialogues(818, 6773);
				break;
		}
	}

}
