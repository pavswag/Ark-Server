package io.kyros.model.entity.player.packets;

import io.kyros.Configuration;
import io.kyros.content.combat.stats.MonsterKillLog;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.flowerpoker.FlowerPoker;

import java.util.Objects;

import static io.kyros.Server.getPlayers;

public class PlayerOptionsHandler implements PacketType {

	@Override
	public void processPacket(Player player, int opCode, int opSize) {

		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;

		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}

		player.interruptActions();

		int index = player.getInStream().readUnsignedWord();

		if (index > getPlayers().size() || index < 0)
			return;

		if (getPlayers().get(index) == null)
			return;

        Player requested = getPlayers().get(index);

		if (Objects.isNull(requested))
			return;

		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}

		if (requested.getBankPin().requiresUnlock()) {
			return;
		}

		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}

		if (requested.getInterfaceEvent().isActive()) {
			player.sendMessage("That player is busy right now.");
			return;
		}

		player.faceEntity(requested);


		switch (opCode) {

		case 128:
			/**
			 * Duel challenge / Flower Poker
			 */

			if (Boundary.isIn(player, Boundary.DUEL_ARENA) || Boundary.isIn(requested, Boundary.DUEL_ARENA)) {
				player.sendMessage("You cannot do this inside of the duel arena.");
				return;
			}

			if (Boundary.isIn(player, FlowerPoker.BOUNDARIES)) {
				if (Boundary.isIn(requested, FlowerPoker.BOUNDARIES)) {
					if (player.getFlowerPokerRequest().requestable(requested)) {
						player.getFlowerPokerRequest().request(requested);
						return;
					}
				}
				return;
			}

			if (requested.getPosition().inDuelArena()) {
				if (!Boundary.isIn(player, Boundary.DUEL_ARENA)) {
					if (!Configuration.NEW_DUEL_ARENA_ACTIVE) {
						player.getDH().sendStatement("@red@Dueling Temporarily Disabled", "The duel arena minigame is currently being rewritten.",
								"No player has access to this minigame during this time.", "", "Thank you for your patience, Developer J.");
						player.nextChat = -1;
						return;
					}
					if (player.getDuel().requestable(requested)) {
						player.getDuel().request(requested);
					}
				}
			}

			if (MonsterKillLog.onPlayerOption(player,requested,"PlayerOptions") && !Boundary.isIn(player, FlowerPoker.BOUNDARIES)) {
				return;
			}
			return;
		}
	}
}
