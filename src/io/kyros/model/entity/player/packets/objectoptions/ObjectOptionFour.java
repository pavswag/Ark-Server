package io.kyros.model.entity.player.packets.objectoptions;

import io.kyros.Server;
import io.kyros.content.dialogue.impl.OutlastLeaderboard;
import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.objects.ObjectAction;

public class ObjectOptionFour {
	
	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;

		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);

		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 4:  "+objectType+"");

		ObjectDef def = ObjectDef.getObjectDef(objectType);
		ObjectAction action = null;
		ObjectAction[] actions = def.defaultActions;
		if(actions != null)
			action = actions[3];
		if(action == null && (actions = def.defaultActions) != null)
			action = actions[3];
		if(action != null) {
			action.handle(c, object);
			return;
		}

		if (OutlastLeaderboard.handleInteraction(c, objectType, 4))
			return;

		switch (objectType) {
		case 31858:
		case 29150:
			c.setSidebarInterface(6, 938);
			c.playerMagicBook = 0;
			c.sendMessage("You feel a drain on your memory.");
			break;
		case 8356://streehosidius
			c.getPA().movePlayer(1679, 3541, 0);
			break;
		}
	}

}
