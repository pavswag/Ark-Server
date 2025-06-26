package io.kyros.model.entity.player.packets;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.CompletionistCape;
import io.kyros.content.DiceHandler;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.skills.runecrafting.Pouches;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;

import java.util.Objects;

/**
 * Wear Item
 **/
public class WearItem implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		c.interruptActions();
		int wearId = c.wearId;
		wearId = c.getInStream().readUnsignedWord();
		c.wearSlot = c.getInStream().readUnsignedWordA();
		c.wearItemInterfaceId = c.getInStream().readUnsignedWordA();
		c.alchDelay = System.currentTimeMillis();
		c.nextChat = 0;
		c.dialogueOptions = 0;
		c.graniteMaulSpecialCharges = 0;
		if (c.debugMessage) {
			c.sendMessage(String.format("WearItem[item=%d]", wearId));
		}

		if (wearId == 22817 && !Boundary.isIn(c, Boundary.LAKE_MOLCH)) {
			c.getItems().deleteItem2(22817,1);
			c.sendMessage("@red@Alry's bird fly's home, stop trying to steal the fucking bird!");
			return;
		}

		if (!c.getItems().playerHasItem(wearId, 1)) {
			return;
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
			return;
		}
		if (c.isStuck) {
			c.isStuck = false;
			c.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			c.sendMessage("You cannot remove items from your equipment whilst trading, trade declined.");
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		if ((c.playerAttackingIndex > 0 || c.npcAttackingIndex > 0) && wearId != 4153 && wearId != 12848 && wearId != 24225 && wearId != 24227 && !c.usingMagic && !c.usingBow && !c.usingOtherRangeWeapons && !c.usingCross && !c.usingBallista)
			c.attacking.reset();
		if (c.canChangeAppearance) {
			c.sendMessage("You can't wear an item while changing appearence.");
			return;
		}

		if (LootingBag.isLootingBag(wearId)) {
			c.getLootingBag().openWithdrawalMode();
			return;
		}

		if (wearId == Items.COMPLETIONIST_CAPE && !CompletionistCape.hasRequirements(c)) {
			c.sendMessage("You don't have the requirements to wear that, see Mac to view the requirements.");
			return;
		}

		if (wearId == 4155) {
			if (!c.getSlayer().getTask().isPresent()) {
				c.sendMessage("You do not have a task!");
				return;
			}
			c.sendMessage("I currently have @blu@" + c.getSlayer().getTaskAmount() + " " + c.getSlayer().getTask().get().getPrimaryName() + "@bla@ to kill.");
			c.getPA().closeAllWindows();
			return;
			
		}
		if (wearId == 23351) {
			c.isSkulled = true;
			c.skullTimer = Configuration.SKULL_TIMER;
			c.headIconPk = 0;
			c.sendMessage("@blu@The @red@Cape of skulls@blu@ has automatically made you skull for @yel@20 minutes.");
		}
		switch (wearId) {
		case 21347:
			c.boltTips = true;
			c.arrowTips = false;
			c.javelinHeads = false;
			c.sendMessage("Your Amethyst method is now Bolt Tips!");
			break;
		case 5509:
			Pouches.empty(c, 0);
			break;
		case 5510:
			Pouches.empty(c, 1);
			break;
		case 5512:
			Pouches.empty(c, 2);
			break;
		}
		
		if (wearId == DiceHandler.DICE_BAG) {
			DiceHandler.selectDice(c, wearId);
		}
		if (wearId > DiceHandler.DICE_BAG && wearId <= 15100) {
			DiceHandler.rollDice(c);
		}


		if (!Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.getPlayerAssistant().resetFollow();
			c.attacking.reset();
			c.getItems().equipItem(wearId, c.wearSlot);
		}
	}

}
