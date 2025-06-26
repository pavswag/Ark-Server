package io.kyros.model.entity.player.packets;

import java.util.Objects;

import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;

import static io.kyros.Server.getPlayers;

/**
 * Trading
 */
public class Trade implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		if (c.getMovementState().isLocked() || c.getLock().cannotInteract(c))
			return;
		c.interruptActions();
		int tradeId = c.getInStream().readSignedWordBigEndian();
		if (tradeId < 0 || tradeId > getPlayers().size()) {
			return;
		}

        Player requested = getPlayers().get(tradeId);
		if (requested == null) {
			return;
		}
		if (c.isNpc) {
			return;
		}
		c.getPA().resetFollow();

		if (c.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		if (requested.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			c.sendMessage("Other player is busy");
			return;
		}
		if (c.getBankPin().requiresUnlock()) {
			c.getBankPin().open(2);
			return;
		}
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe() || CastleWarsLobby.isInCw(c)) {
			c.stopMovement();
			c.sendMessage("@cr10@You cannot trade from here.");
			return;
		}
		if (Boundary.isIn(c, Boundary.DUEL_ARENA)) {
			c.sendMessage("You cannot trade whilst inside the duel arena.");
			return;
		}
		if (Objects.equals(requested, c)) {
			c.sendMessage("You cannot trade yourself.");
			return;
		}
		if (Boundary.isIn(c, Boundary.OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
				|| Boundary.isIn(c, Boundary.FOREST_OUTLAST)
				|| Boundary.isIn(c, Boundary.SNOW_OUTLAST)
				|| Boundary.isIn(c, Boundary.ROCK_OUTLAST)
				|| CastleWarsLobby.isInCw(c) || CastleWarsLobby.isInCwWait(c)
				|| Boundary.isIn(c, Boundary.FALLY_OUTLAST)
				|| Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
				|| Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
				|| Boundary.isIn(c, Boundary.WG_Boundary)) {
			c.sendMessage("You cannot trade in the arena.");
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}
		if (requested.getInterfaceEvent().isActive()) {
			c.sendMessage("That player needs to finish what they're doing.");
			return;
		}
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}

		if (c.getTrade().requestable(requested)) {
			c.getTrade().request(requested);
			return;
		}
	}

}