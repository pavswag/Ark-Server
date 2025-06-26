package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.LootValue;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.items.UseItem;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.skills.TabletCreation;
import io.kyros.model.ContainerAction;
import io.kyros.model.ContainerActionType;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.group.GroupIronmanBank;
import io.kyros.model.items.GameItem;
import io.kyros.model.multiplayersession.MultiplayerSession;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.model.multiplayersession.flowerpoker.FlowerPokerSession;
import io.kyros.model.multiplayersession.trade.TradeSession;
import io.kyros.model.world.ShopHandler;
import io.kyros.util.logging.player.ReceivedPacketLog;

import java.util.Objects;

/**
 * Bank X Items
 **/
public class EnterAmountInput implements PacketType {
	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		int Xamount = c.getInStream().readInteger();
		ContainerAction action = new ContainerAction(ContainerActionType.X, c.xInterfaceId, c.xRemoveId, c.xRemoveSlot, Xamount);


		if (c.debugMessage)
			c.sendMessage("Enter X: interface id: " + c.xInterfaceId + ", amount: "+Xamount+", removeId: "+c.xRemoveId+", item: " + c.item);

		Server.pluginManager.triggerEvent(new io.kyros.script.event.impl.EnterAmountInput(c, Xamount, c.xInterfaceId));
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "Enter X: interface id: " + c.xInterfaceId + ", amount: "+Xamount+", removeId: "+c.xRemoveId+", item: " + c.item));

		if (Xamount < 0) {
			Xamount = c.getItems().getItemAmount(c.xRemoveId);
		}
		if (Xamount == 0) {
			Xamount = 1;
		}
		if (Xamount > Integer.MAX_VALUE) {
			Xamount = 1;
		}

		if (c.amountInputHandler != null) {
			c.amountInputHandler.handle(c, Xamount);
			return;
		}

		switch (c.getEnterAmountInterfaceId()) {
			case LootingBag.DEPOSIT_INTERFACE_ID:
			case LootingBag.WITHDRAW_INTERFACE_ID:
				c.getLootingBag().handleClickItem(c.getLootingBag().getSelectedItem(), Xamount);
				break;
		}
		c.setEnterAmountInterfaceId(0);

		if (c.buyingX) {
			int amount = Xamount;
			if(c.getPosition().inWild() || c.getPosition().inClanWars()) { //Fix wildy resource zone here inwild() && !inwildyzone
				return;
			}

			if (amount > 1000000) {
				c.sendMessage("You can only buy 1,000,000 items at a time.");
				amount = 1000000;
			}
			if (c.xInterfaceId == 64016) {
				c.xRemoveId = ShopHandler.ShopItems[c.myShopId][c.xRemoveSlot]-1;
			}
			c.getShops().buyItem(c.xRemoveId, c.xRemoveSlot, amount);// buy X
            c.xRemoveSlot = 0;
            c.xInterfaceId = 0;
            c.xRemoveId = 0;
			c.buyingX = false;
            //return;
		}
		if (c.sellingX) {
			int amount = Xamount;
	    	if(c.getPosition().inWild() || c.getPosition().inClanWars()) {
				return;
			}
			if (Boundary.isIn(c, Boundary.TOURNAMENT_LOBBIES_AND_AREAS)) {
				c.sendMessage("You cannot do this right now.");
				return;
			}
	    	c.getShops().sellItem(c.xRemoveId, c.xRemoveSlot, amount);// sell X
	        c.xRemoveSlot = 0;
	        c.xInterfaceId = 0;
	        c.xRemoveId = 0;
			c.sellingX = false;
	        //return;
		}
		final int amount2 = Xamount;
		c.getFletching().getSelectedFletchable().ifPresent(fletchable -> {
			c.getFletching().fletchLog(fletchable, amount2);
			return;
		});
		if (c.viewingRunePouch) {
			if (c.getRunePouch().finishEnterAmount(Xamount)) {
				return;
			}
		}

		if (c.inBank) {
			if (c.getBank().withdrawFromSlot(c.xInterfaceId, c.xRemoveId, c.xRemoveSlot, Xamount)) {
				c.xRemoveSlot = 0;
				c.xInterfaceId = 0;
				c.xRemoveId = 0;
				return;
			}
		} else {
			if (GroupIronmanBank.processContainerAction(c, action)) {
				c.xRemoveSlot = 0;
				c.xInterfaceId = 0;
				c.xRemoveId = 0;
				return;
			}
		}

		switch (c.xInterfaceId) {


		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				c.sendMessage("You cannot bank items whilst trading.");
				return;
			}
			if (!c.getItems().playerHasItem(c.xRemoveId, Xamount))
				return;
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
					&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(c.playerItems[c.xRemoveSlot] - 1, Xamount, true);
			}
			break;

		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.addItem(c, new GameItem(c.xRemoveId, Xamount));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, c.xRemoveSlot, new GameItem(c.xRemoveId, Xamount));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession) {
				session.removeItem(c, c.xRemoveSlot, new GameItem(c.xRemoveId, Xamount));
			}
			break;
		}
		if (c.settingMin) {
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			c.diceMin = Xamount;
			c.settingMin = false;
			c.settingMax = true;
			c.getDH().sendDialogues(9998, -1);
			return;
		} else if (c.settingMax) {
			if (Xamount < 0 || Xamount > Integer.MAX_VALUE)
				return;
			c.diceMax = Xamount;
			c.settingMax = false;
			c.settingMin = false;
			c.getDH().sendDialogues(9999, -1);
			return;
		}
		if (c.attackSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 0;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.attackSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.defenceSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}
			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 1;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getCombatPrayer().resetPrayers();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.defenceSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.strengthSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}

			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 2;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.strengthSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.healthSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}

			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 3;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.healthSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.rangeSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}

			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 4;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.rangeSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.prayerSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}

			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				c.getCombatPrayer().resetPrayers();
				int skill = 5;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.prayerSkill = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (c.mageSkill) {
			if (c.getPosition().inWild() || c.getPosition().inDuelArena()) {
				c.sendMessage("You cannot change levels here.");
				return;
			}

			for (int j = 0; j < c.playerEquipment.length; j++) {
				if (c.playerEquipment[j] > 0) {
					c.sendMessage("Please remove all your equipment before setting your levels.");
					return;
				}
			}
			try {
				int skill = 6;
				int level = Xamount;
				if (level > 99)
					level = 99;
				else if (level < 0)
					level = 1;
				c.playerXP[skill] = c.getPA().getXPForLevel(level) + 5;
				c.playerLevel[skill] = c.getPA().getLevelForXP(c.playerXP[skill]);
				c.getPA().refreshSkill(skill);
				c.combatLevel = c.calculateCombatLevel();
				c.getPA().sendFrame126("Combat Level: " + c.combatLevel + "", 3983);
				c.getPA().requestUpdates();
				c.mageSkill = false;
			} catch (Exception e) {
			}
		}
		if (c.boneOnAltar) {
			if (c.getPrayer().getAltarBone().isPresent()) {
				c.getPrayer().alter(Xamount, c.objectX, c.objectY);
				return;
			}
		}
		if (c.settingLootValue) {
			LootValue.configureValue(c, "setvalue", Xamount);
		}
		if (c.settingUnnoteAmount) {
			if (Xamount < 1) {
				UseItem.unNoteItems(c, c.unNoteItemId, 1);
			} else {
				UseItem.unNoteItems(c, c.unNoteItemId, Xamount);
			}
		}
		switch (c.tablet) {
		case 1:
			c.getPA().closeAllWindows();
			c.tablet = 0;
			if (Xamount > 100) {
				c.sendMessage("You may only create 100 at a time.");
				return;
			}

			try {
				TabletCreation.createTablet(c, 0, Xamount);
			} catch (Exception e) {
			}
			break;
		case 2:
			c.getPA().closeAllWindows();
			c.tablet = 0;
			if (Xamount > 100) {
				c.sendMessage("You may only create 100 at a time.");
				return;
			}
			try {
				TabletCreation.createTablet(c, 1, Xamount);
			} catch (Exception e) {
			}
			break;
			
		case 3:
			c.sendMessage("This");
			break;
		}
	}
}