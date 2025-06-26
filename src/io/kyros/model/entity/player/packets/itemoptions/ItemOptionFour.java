package io.kyros.model.entity.player.packets.itemoptions;

import java.util.Objects;
import java.util.Optional;

import io.kyros.Server;
import io.kyros.content.combat.magic.SanguinestiStaff;
import io.kyros.content.items.Degrade;
import io.kyros.content.items.Degrade.DegradableItem;
import io.kyros.content.items.pouch.RunePouch;
import io.kyros.content.teleportation.TeleportTablets;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAction;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.util.Misc;

/**
 * Item Click 3 Or Alternative Item Option 1
 * 
 * @author Ryan / Lmctruck30
 * 
 *         Proper Streams
 */

public class ItemOptionFour implements PacketType {


	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked())
			return;
		c.interruptActions();
		int itemId11 = c.getInStream().readSignedWordBigEndianA();
		int itemId1 = c.getInStream().readSignedWordA();
		int itemId = c.getInStream().readSignedWordA();

		if (c.debugMessage) {
			c.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 4, -1, -1));
		}
		GameItem gameItem = new GameItem(itemId);
		ItemDef itemDef = ItemDef.forId(itemId);
		ItemAction action = null;
		ItemAction[] actions = itemDef.inventoryActions;
		if(actions != null)
			action = actions[3];
		if(action != null) {
			action.handle(c, gameItem);
			return;
		}

		if (c.getLock().cannotClickItem(c, itemId))
			return;
		if (!c.getItems().playerHasItem(itemId, 1)) {
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (RunePouch.isRunePouch(itemId)) {
			c.getRunePouch().emptyBagToInventory();
			return;
		}
		TeleportTablets.operate(c, itemId);
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
		Optional<DegradableItem> d = DegradableItem.forId(itemId);
		if (d.isPresent()) {
			Degrade.checkPercentage(c, itemId);
			return;
		}
		if (SanguinestiStaff.clickItem(c, itemId, 4)) {
			return;
		}
		Server.pluginManager.triggerEvent(new io.kyros.script.event.impl.ItemAction(c, itemId, 4));

		switch (itemId) {

		default:
			if (c.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
				Misc.println("[DEBUG] Item Option #4-> Item id: " + itemId);
			}
			break;
		}

	}

}
