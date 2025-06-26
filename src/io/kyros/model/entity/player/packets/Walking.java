package io.kyros.model.entity.player.packets;

import java.nio.file.Path;
import java.util.Optional;

import io.kyros.Server;
import io.kyros.content.donor.NomadVault;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.content.skills.SkillHandler;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.model.multiplayersession.duel.DuelSessionRules;
import io.kyros.model.world.Clan;
import io.kyros.util.Misc;

/**
 * Walking packet
 **/
public class Walking implements PacketType {

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
		c.nextChat = 0;
		c.dialogueOptions = 0;
		c.homeTeleport = 50;
		c.interruptActions();
		if (c.isDead || c.getHealth().getCurrentHealth() <= 0) {
			c.sendMessage("You are dead you cannot walk.");
			return;
		}
		if (c.isIdle) {
			if (c.debugMessage)
				c.sendMessage("You are no longer in idle mode.");
			c.isIdle = false;
			if (c.clan == null) {
				Clan clan = Server.clanManager.getClan("help");
				if (clan != null) {
					clan.addMember(c);
				}
				c.getPA().refreshSkill(21);
				c.getPA().refreshSkill(22);
				c.getPA().refreshSkill(23);
			}
		}
		if (!c.getMovementState().isAllowClickToMove()) {
			return;
		}
		if (c.isForceMovementActive()) {
			return;
		}
		if (!c.getPosition().inClanWars() && !c.getPosition().inClanWarsSafe() && c.pkDistrict) {
			c.sendMessage("You did not leave the district properly, therefore your items have been deleted.");
			c.getItems().deleteAllItems();
		}
		if (c.rottenPotatoOption != "") {
			c.rottenPotatoOption = "";
		}
		if (c.getInferno() != null && c.getInferno().cutsceneWalkBlock)
			return;
		if (c.morphed) {
			c.sendMessage("You cannot do this now.");
			return;
		}
		if (c.getCurrentCombination().isPresent()) {
			c.setCurrentCombination(Optional.empty());
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
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

		if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
			c.sendMessage("You must decline the trade to start walking.");
			return;
		}
		if (c.freezeTimer > 0) {
			c.sendMessage("A magical force stops you from moving.");
			return;
		}
		if(c.isInTradingPost()) {
			c.inTradingPost = false;
			c.clickDelay = System.currentTimeMillis();
		}
		if(c.inBank) {
			c.inBank = false;
			c.clickDelay = System.currentTimeMillis();
		}
		if(c.inPresets) {
			c.inPresets = false;
			c.clickDelay = System.currentTimeMillis();
		}
		if(c.inLamp) {
			c.inLamp = false;
			c.clickDelay = System.currentTimeMillis();
		}
		if (c.afk_position != null) {
			c.afk_position = null;
			if (c.debugMessage)
				c.sendErrorMessage("Your afk position is now null");
		}
		DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
		if (session != null && session.getStage().getStage() == MultiplayerSessionStage.FURTHER_INTERATION && !Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			if (session.getRules().contains(DuelSessionRules.Rule.NO_MOVEMENT)) {
				Player opponent = session.getOther(c);
				if (Boundary.isIn(opponent, session.getArenaBoundary())) {
					c.getPA().movePlayer(opponent.getX(), opponent.getY() - 1, 0);
				} else {
					int x = session.getArenaBoundary().getMinimumX() + 6 + Misc.random(12);
					int y = session.getArenaBoundary().getMinimumY() + 1 + Misc.random(11);
					c.getPA().movePlayer(x, y, 0);
					opponent.getPA().movePlayer(x, y - 1, 0);
				}
			} else {
				c.getPA().movePlayer(session.getArenaBoundary().getMinimumX() + 6 + Misc.random(12), session.getArenaBoundary().getMinimumY() + 1 + Misc.random(11), 0);
			}
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			if (session == null) {
				c.getPA().movePlayer(3362, 3264, 0);
				return;
			}
			if (session.getRules().contains(DuelSessionRules.Rule.NO_MOVEMENT)) {
				c.sendMessage("Movement has been disabled for this duel.");
				return;
			}
		}
		if (session != null && session.getStage().getStage() > MultiplayerSessionStage.REQUEST && session.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			c.sendMessage("You have declined the duel.");
			session.getOther(c).sendMessage("The challenger has declined the duel.");
			session.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}

		if (Boundary.isIn(c, Boundary.ICE_PATH)) {
			c.updateRunningToggled(false);
		}
		if (c.canChangeAppearance) {
			c.canChangeAppearance = false;
		}

		c.getPA().stopSkilling();
		c.getPA().resetVariables();
		SkillHandler.isSkilling[12] = false;

		if (c.teleporting) {
			c.startAnimation(65535);
			c.teleporting = false;
			c.gfx0(-1);
			c.startAnimation(-1);
		}

		c.walkingToItem = false;
		c.isWc = false;
		c.clickNpcType = 0;
		c.clickObjectType = 0;
		if (c.isBanking)
			c.isBanking = false;
		if (c.tradeStatus >= 0) {
			c.tradeStatus = 0;
		}

		if (packetType == 248 || packetType == 164) {
			c.faceUpdate(0);
			c.attacking.reset();
		}

		c.getPA().removeAllWindows();
		if (c.stopPlayerSkill) {
			SkillHandler.resetPlayerSkillVariables(c);
			c.stopPlayerSkill = false;
		}

		if (c.respawnTimer > 3 || c.inTrade || c.teleTimer > 0) {
			return;
		}

		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			c.getInterfaceEvent().draw();
			return;
		}

		if (c.isFping()) {
			System.err.println("Cannot move while in an active fp session!");
			return;
		}

		if (!c.canRollBox(c)) {
			c.getPA().showInterface(47000);
			c.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
			return;
		}

		if (c.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
			if (!Boundary.isIn(c, Boundary.WILDERNESS) &&
					!Boundary.isIn(c, Boundary.WILDERNESS_UNDERGROUND) &&
					!Boundary.isIn(c, Boundary.WILDERNESS_GOD_WARS_BOUNDARY) &&
					!Boundary.isIn(c, Boundary.REV_CAVE) &&
					!Boundary.isIn(c, Boundary.Wilderness_Slayer) &&
					!Boundary.isIn(c, Boundary.DONATOR_ZONE) &&
					!Boundary.isIn(c, NomadVault.area) &&
					!Boundary.isIn(c, Boundary.AFK_ZONE) &&
					!Boundary.isIn(c, Boundary.Ferox1) &&
					!Boundary.isIn(c, Boundary.Ferox2) &&
					!Boundary.isIn(c, Boundary.Ferox3) &&
					!Boundary.isIn(c, Boundary.Ferox4) &&
					!Boundary.isIn(c, Boundary.Ferox5) &&
					!Boundary.isIn(c, Boundary.Ferox6) &&
					!Boundary.isIn(c, Boundary.Ferox7) &&
					!Boundary.isIn(c, Boundary.Ferox8) &&
					!Boundary.isIn(c, Boundary.Ferox9) &&
					!Boundary.isIn(c, Boundary.Ferox10) &&
					!Boundary.isIn(c, Boundary.Ferox11) &&
					!Boundary.isIn(c, Boundary.Ferox12) &&
					!Boundary.isIn(c, Boundary.Ferox13) &&
					!Boundary.isIn(c, Boundary.PERK_ZONE) &&
					!Boundary.isIn(c, Boundary.GROOT_BOSS) &&
					!Boundary.isIn(c, Boundary.VOTE_BOSS) &&
					!Boundary.isIn(c, Boundary.FIGHT_CAVE) &&
					!Boundary.isIn(c, Boundary.UNICOW_AREA) &&
					!Boundary.isIn(c, HotDrops.BOUNDARY) &&
					!Boundary.isIn(c, new Boundary(2368, 3776, 2431, 3903)) &&
			        !Boundary.isIn(c, Boundary.CRYSTAL_CAVE_AREA) &&
			        !Boundary.isIn(c, Boundary.CRYSTAL_CAVE_ENTRANCE) &&
			        !Boundary.isIn(c,Boundary.CRYSTAL_CAVE_STAIRS) &&
			        !Boundary.isIn(c, Boundary.ABYSS_RC) &&
			        !Boundary.isIn(c, Boundary.ESSENCE_MINE) &&
			        !Boundary.isIn(c, Boundary.HUNLLEF_CHEST) &&
			        !Boundary.isIn(c, Boundary.MAX_ISLAND) ){
				c.moveTo(new Position(3135, 3628, 0));
				return;
			}
		}

		c.startAnimation(-1);
		c.viewingPresets = false;
		c.closedInterface();
		if (packetType == 248) {
			packetSize -= 14;
		}

		int steps = (packetSize - 5) / 2;
		int[][] path = new int[steps][2];

		int firstStepX = c.getInStream().readSignedWordBigEndianA();

		for (int i = 0; i < steps; i++) {
			path[i][0] = c.getInStream().readSignedByte();
			path[i][1] = c.getInStream().readSignedByte();
		}

		int firstStepY = c.getInStream().readSignedWordBigEndian();
		boolean runPath = c.getInStream().readSignedByteC() == 1;

		for (int i = 0; i < steps; i++) {
			path[i][0] += firstStepX;
			path[i][1] += firstStepY;
		}

		int pathX = steps > 0 ? path[(steps - 1)][0] : firstStepX;
		int pathY = steps > 0 ? path[(steps - 1)][1] : firstStepY;
		if (Misc.distance(c.absX, c.absY, pathX, pathY) < 35) {
			PathFinder.getPathFinder().findRoute(c, pathX, pathY, true, 0, 0);
			c.setNewWalkCmdIsRunning(runPath);
		}
	}

}