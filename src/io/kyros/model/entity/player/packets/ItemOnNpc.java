package io.kyros.model.entity.player.packets;

import java.util.Objects;

import io.kyros.Server;
import io.kyros.content.bosses.dukesucellus.DukeInstance;
import io.kyros.content.bosses.dukesucellus.DukeSucellus;
import io.kyros.content.items.UseItem;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;

import static io.kyros.Server.getNpcs;

public class ItemOnNpc implements PacketType {

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
		int itemId = c.getInStream().readSignedWordA();
		int npcIndex = c.getInStream().readSignedWordA();
		int slot = c.getInStream().readSignedWordBigEndian();

		if (c.getItems().getInventoryCount(itemId) < 1)
			return;

        NPC npc = getNpcs().get(npcIndex);

		if (npc == null)
			return;

		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}

		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
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

		if (npc.getNpcId() == 12166) {
			if (c.getItems().getInventoryCount(28351) >= 2) {  // Check if player has the wake-up potion
				// Check if the player is in the same instance as the Duke NPC
				if (c.getInstance() != null && c.getInstance().equals(npc.getInstance())) {
					DukeInstance ins = (DukeInstance) c.getInstance();  // Cast instance to DukeInstance
					if (ins != null) {
						ins.wakeDuke(c);  // Call wakeDuke method on the instance
						c.resetWalkingQueue();
					}
				} else {
					c.sendMessage("You must be in the same instance to wake Duke Sucellus.");
				}
			} else {
				c.sendErrorMessage("Looks like you don't have 2 potions to wake duke!");
			}
			return;
		}


		if (npc.distance(c.getPosition()) <= 1) {
			completeItemOnNpc(c, npc, itemId, slot);
		} else {
			c.getPA().followNPC(npc, false);
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (npc.distance(c.getPosition()) <= 1) {
						completeItemOnNpc(c, npc, itemId, slot);
						container.stop();
					}
				}

				@Override
				public void onStopped() {
					c.getPA().resetFollow();
				}
			}, 1);
		}
	}

	private void completeItemOnNpc(Player c, NPC npc, int itemId, int itemSlot) {
		c.facePosition(npc.getX(), npc.getY());
		npc.facePlayer(c.getIndex());
		c.faceUpdate(0);
		c.getPA().resetFollow();
		if (!c.getItems().playerHasItem(itemId, 1))
			return;
		UseItem.handleItemOnNPC(c, npc, itemId, itemSlot);
	}
}
