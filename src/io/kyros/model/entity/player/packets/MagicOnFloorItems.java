package io.kyros.model.entity.player.packets;

import java.util.Objects;

import io.kyros.Server;
import io.kyros.content.combat.magic.MagicRequirements;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;

/**
 * Magic on floor items
 **/
public class MagicOnFloorItems implements PacketType {

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
		int itemY = c.getInStream().readSignedWordBigEndian();
		int itemId = c.getInStream().readUnsignedWord();
		int itemX = c.getInStream().readSignedWordBigEndian();
		int spellId = c.getInStream().readUnsignedWordA();

		if (!Server.itemHandler.itemExists(c, itemId, itemX, itemY, c.heightLevel)) {
			c.stopMovement();
			return;
		}
		c.usingMagic = true;
		if (!MagicRequirements.checkMagicReqs(c, 51, true)) {
			c.stopMovement();
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
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}
/*		if (c.goodDistance(c.getX(), c.getY(), itemX, itemY, 12)) {
			if (System.currentTimeMillis() - c.clickDelay <= 2200) {
				return;
			}
			GroundItem item = Server.itemHandler.getGroundItem(c, itemId, itemX, itemY, c.heightLevel);
			if (item != null) {
				if (!c.getMode().isItemScavengingPermitted()) {
					Player owner = PlayerHandler.getPlayerByLoginName(item.getOwnerName());
					GroupIronmanGroup group = GroupIronmanRepository.getGroupForOnline(c).orElse(null);
					if (owner == null || group == null && !c.getLoginNameLower().equalsIgnoreCase(item.getOwnerName()) || group != null && c.isApartOfGroupIronmanGroup() && !group.isGroupMember(owner)) {
						c.sendMessage("Your mode restricts you from picking up items that are not yours.");
						return;
					}
				}
				if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
					Player owner = PlayerHandler.getPlayerByLoginName(item.getOwnerName());
					if (owner != c) {
						c.sendMessage("You cannot collect an item you do not own!");
						return;
					}
				}

				if (c.getInterfaceEvent().isActive()) {
					c.sendMessage("Please finish what you're doing.");
					return;
				}
				if (c.getPA().viewingOtherBank) {
					c.getPA().resetOtherBank();
				}
				c.attacking.reset();
				if (c.teleportingToDistrict) {
					return;
				}
				if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
					if (!c.pkDistrict) {
						return;
					}
				}

				if (Server.itemHandler.getGroundItem(c, itemId, itemX, itemY, c.heightLevel).isLocked()) {
					return;
				}

				if (c.isDead || c.respawnTimer > -6) {
					return;
				}

				for (int i : new int[] {20370, 20374, 20372, 20368, 27275, 33205,  33058, 33207, 27235,
						27238, 27241, 33141, 33142, 33143, 33202, 33204, 28688, 28254, 28256, 28258, 26269, 26551,
						10559, 10556, 26914, 33160, 33161, 33162, 25739, 25736, 26708, 24664, 24666, 24668,
						25918, 26482, 26484, 25734, 26486, 33184, 33203, 33206, 27428, 27430, 27432, 27434, 27436, 27438,
						33149, 26477, 26475, 26473, 26467, 33189, 33190, 33191, 27253, 33183, 33186, 33187, 33188, 12899, 12900,
						26469, 26471, 13681, 26235, 12892,12893,12894,12895,12896,33064,33059,33063,33060,33062,33061, 27473, 27475, 27477, 27479, 27481}) {
					if (itemId == i) {
						return;
					}
				}

				if (Boundary.isIn(c, Boundary.TELEGRAB_WILDYEDGE)) {
					return;
				}

				c.stopMovement();
				int offY = (c.getX() - itemX) * -1;
				int offX = (c.getY() - itemY) * -1;

				c.clickDelay = System.currentTimeMillis();
				c.lock(new CompleteLock());
				Server.itemHandler.getGroundItem(c, itemId, itemX, itemY, c.heightLevel).setLocked(true);
				c.facePosition(itemX, itemY);
				c.teleGrabDelay = System.currentTimeMillis();
				c.startAnimation(CombatSpellData.MAGIC_SPELLS[51][2]); c.gfx100(CombatSpellData.MAGIC_SPELLS[51][3]);
				c.getPA().createPlayersProjectile(c.getX(), c.getY(), offX, offY, 50, 70, CombatSpellData.MAGIC_SPELLS[51][4], 50, 10, 0, 50);
				CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
					@Override
					public void execute(CycleEventContainer container) {
						if (!Server.itemHandler.itemExists(c, itemId, itemX, itemY, c.heightLevel)) {
							c.sendMessage("It's gone!");
							container.stop();
							return;
						}

						if (container.getTotalExecutions() == 2) {
							c.getPA().createPlayersStillGfx(144, itemX, itemY, 0, 72);
						}
						if (container.getTotalExecutions() == 4) {
							c.getPA().addSkillXP(CombatSpellData.MAGIC_SPELLS[51][7], 6, true);
							c.getPA().refreshSkill(6);
							Server.itemHandler.getGroundItem(c, itemId, itemX, itemY, c.heightLevel).setLocked(false);
							Server.itemHandler.removeGroundItem(c, item, true);
							c.lock(new Unlocked());
						}
					}
				}, 1);
			}
		}*/
	}

}
