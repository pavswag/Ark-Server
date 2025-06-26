package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.ItemSpawner;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.preset.PresetManager;
import io.kyros.content.skills.crafting.JewelryMaking;
import io.kyros.content.skills.smithing.Smithing;
import io.kyros.model.ContainerAction;
import io.kyros.model.ContainerActionType;
import io.kyros.model.entity.player.CosmeticOverride;
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
import io.kyros.script.event.impl.ItemContainerOption;
import io.kyros.sql.ingamestore.StoreInterface;
import io.kyros.util.logging.player.ReceivedPacketLog;

import java.util.Objects;

/**
 * Bank 5 Items
 **/
public class ContainerAction2 implements PacketType {

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
		int interfaceId = c.getInStream().readUnsignedWord();
		int removeId = c.getInStream().readUnsignedWord();
		int removeSlot = c.getInStream().readSignedWordBigEndian();

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_2, interfaceId, removeId, removeSlot);

		if (c.debugMessage)
			c.sendMessage("ContainerAction2: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);

		//Server.pluginManager.triggerEvent(new ItemContainerOption(c, interfaceId, removeSlot, removeId, 2));
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "i=" + interfaceId + "/" + removeSlot + "/" + removeId));
		if (CosmeticOverride.handleToggleCosmetic(c, interfaceId)) {
			return;
		}

		if(interfaceId == 12463) {
			EventManager.add(c, removeId, 5);
		}
		if (interfaceId >= 60500 && interfaceId < 60700) {
			c.cart.getItems().clear();
			StoreInterface.openInterface(c);
			return;
		}
		if (c.getInterfaceEvent().isActive()) {
			c.sendMessage("Please finish what you're doing.");
			return;
		}

		if (interfaceId == 48500) {
			c.getTradePost().handleInput(interfaceId, 2, removeId);
			return;
		}
		if (interfaceId == 26022) {
			c.getTradePost().handleInput(interfaceId, 2, removeSlot);
			return;
		}

		if (c.viewingPresets) {
			PresetManager.getSingleton().addItemToEquipment(c, removeId, c.playerItemsN[removeSlot]);
			return;
		}
		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, 5)) {
				return;
			}
		}
		if (c.viewingRunePouch) {
			if (c.getRunePouch().handleClickItem(removeId, 5, interfaceId)) {
				return;
			}
		}

		if (c.getBank().withdraw(interfaceId, removeId, 5)) {
			return;
		}
		
		switch (interfaceId) {
		case GroupIronmanBank.INTERFACE_ITEM_CONTAINER_ID:
			GroupIronmanBank.processContainerAction(c, action);
			break;
		case ItemSpawner.CONTAINER_ID:
			ItemSpawner.spawn(c, removeId,5000);
			break;

		case 4233:
		case 4239:
		case 4245:
			JewelryMaking.mouldItem(c, removeId, 5);
			break;

		case 1119:
		case 1120:
		case 1121:
		case 1122:
		case 1123:
			case 2031:
			Smithing.readInput(c.playerLevel[Player.playerSmithing], Integer.toString(removeId), c, 5);
			break;

		case 3900:
			c.getShops().buyItem(removeId, removeSlot, 1);
			break;

		case 64016:
			c.getShops().buyItem(removeId, removeSlot, 1);
			break;

		case 3823:
			c.getShops().sellItem(removeId, removeSlot, 1);
			break;

		case 5064:
			if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
				Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				c.sendMessage("You cannot add items to the bank whilst trading.");
				return;
			}
			DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
			if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
				c.sendMessage("You have declined the duel.");
				duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
				duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
				return;
			}
			if (c.isBanking) {
				c.getItems().addToBank(removeId, 5, true);
			} else {
				GroupIronmanBank.processContainerAction(c, action);
			}
			break;
		case 3322:
			MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.addItem(c, new GameItem(removeId, 5));
			}
			break;

		case 3415:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 5));
			}
			break;

		case 6669:
			session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
			if (Objects.isNull(session)) {
				return;
			}
			if (session instanceof DuelSession || session instanceof FlowerPokerSession) {
				session.removeItem(c, removeSlot, new GameItem(removeId, 5));
			}
			break;

		}
	}

}
