package io.kyros.model.entity.player.packets;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.ItemSpawner;
import io.kyros.content.commands.owner.equip;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.fireofexchange.FireOfExchange;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.content.itemskeptondeath.perdu.PerduLostPropertyShop;
import io.kyros.content.minigames.coinflip.CoinFlip;
import io.kyros.content.preset.PresetManager;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.crafting.JewelryMaking;
import io.kyros.content.skills.smithing.Smithing;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.ContainerAction;
import io.kyros.model.ContainerActionType;
import io.kyros.model.definitions.ItemDef;
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
import io.kyros.model.shops.ShopAssistant;
import io.kyros.sql.ingamestore.StoreInterface;
import io.kyros.util.Misc;
import io.kyros.util.logging.player.ReceivedPacketLog;

import java.util.Objects;
/**
 * Remove Item
 **/
public class ContainerAction1 implements PacketType {

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
		int interfaceId = c.getInStream().readUnsignedWordA();
		int removeSlot = c.getInStream().readUnsignedWordA();
		int removeId = c.getInStream().readUnsignedWordA();

//		c.sendMessage("i=" + interfaceId + "/" + removeSlot + "/" + removeId);
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "i=" + interfaceId + "/" + removeSlot + "/" + removeId));

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_1, interfaceId, removeId, removeSlot);

		if (interfaceId == 7331) {
			c.getPA().closeAllWindows();
			int itemId = removeSlot-1;
			int itemAmount = c.getBank().getItemCountInTabs(removeSlot);
			long coins = 0;
			long foundry = 0;
			if (ShopAssistant.getItemShopValue(itemId) > 0) {
				coins += (long) (ShopAssistant.getItemShopValue(itemId)/3)* itemAmount;
			}
			if (FireOfExchangeBurnPrice.getBurnPrice(c, itemId, true) > 0) {
				foundry += (long) FireOfExchangeBurnPrice.getBurnPrice(c, itemId, true) * itemAmount;
			}
			long finalFoundry = foundry;
			long finalCoins = coins;
			c.start(new DialogueBuilder(c).option("Destroy item for " + (foundry > 0 ? Misc.formatCoins(foundry) + " Nomad Points" : (coins > Integer.MAX_VALUE ? Misc.formatCoins(coins / 1000) + " Platinum" : Misc.formatCoins(coins) + " Coins.")),
					new DialogueOption("Yes", plr -> {
						c.getPA().closeAllWindows();
						if (c.hitStandardRateLimit(true))
							return;
						if (itemId == 995) {
							plr.sendMessage("@red@You can't incinerate coins!");
							return;
						}
						if (itemId == 13204) {
							plr.sendMessage("@red@You can't incinerate coins!");
							return;
						}
						if (!c.getBank().containsItem(removeSlot)) {
							plr.sendMessage("@red@You don't have this item in your bank.");
							return;
						}

						if (finalFoundry > 0) {
							c.foundryPoints += finalFoundry;
						} else if (finalCoins > 0 && finalCoins < Integer.MAX_VALUE) {
							c.getItems().addItemUnderAnyCircumstance(995, (int) finalCoins);
						} else if (finalCoins > Integer.MAX_VALUE && finalCoins < Long.MAX_VALUE) {
							c.getItems().addItemUnderAnyCircumstance(13204, (int) (finalCoins /1000));
						}

						if (c.playerXP[Skill.FORTUNE.getId()] < 0) {
							c.playerLevel[Skill.FORTUNE.getId()] = 99;
							c.playerXP[Skill.FORTUNE.getId()] = c.getPA().getXPForLevel(99) + 1;
							c.getPA().refreshSkill(Skill.FORTUNE.getId());
							c.getPA().setSkillLevel(Skill.FORTUNE.getId(), c.playerLevel[Skill.FORTUNE.getId()], c.playerXP[Skill.FORTUNE.getId()]);
							c.getPA().levelUp(Skill.FORTUNE.getId());
						}

						if (c.playerXP[Skill.FORTUNE.getId()] < 200_000_000) {
							c.getPA().addSkillXPMultiplied(25 * itemAmount, Skill.FORTUNE.getId(), true);
						}

						c.getBank().RemoveBankTabItem(removeSlot);
					}),
					new DialogueOption("No", plr -> plr.getPA().closeAllWindows())));
			return;
		}

		if (interfaceId == 48847) {
			c.getTradePost().handleInput(interfaceId, removeSlot, removeId);
			return;
		}

		if (interfaceId == 48500) {
			c.getTradePost().handleInput(interfaceId, 1, removeId);
			return;
		}

		if (interfaceId == 26022) {
			c.getTradePost().handleInput(interfaceId, 1, removeSlot);
			return;
		}

		if (CosmeticOverride.handleUnequipCosmetic(c, interfaceId)) {
			return;
		}

		if (c.getLootingBag().isWithdrawInterfaceOpen() || c.getLootingBag().isDepositInterfaceOpen()) {
			if (c.getLootingBag().handleClickItem(removeId, 1)) {
				return;
			}
		}
		if (c.getRunePouch().handleClickItem(removeId, 1, interfaceId)) {
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
		if (c.debugMessage)
			c.sendMessage("ContainerAction1: interfaceid: "+interfaceId+", removeSlot: "+removeSlot+", removeID: " + removeId);

		if(interfaceId == 12463) {
			EventManager.add(c, removeId, 1);
		}
		//Server.pluginManager.triggerEvent(new ItemContainerOption(c, interfaceId, removeSlot, removeId, 1));
		if (CoinFlip.handleItemChoice(c, interfaceId, removeSlot, removeId)) {
			return;
		}

		if (interfaceId >= 60700 && interfaceId <= 60940) {
			StoreInterface.addItemtoCart(c, removeId);
			return;
		}
		if (interfaceId >= 60500 && interfaceId < 60700) {
			StoreInterface.removeItemFromCart(c, removeId);
			return;
		}

		if (interfaceId == 65022) {
			c.getPerkSytem().removePerk(removeId);
			return;
		}
		if (PerduLostPropertyShop.handleContainerAction(c, action))
			return;
		if (c.viewingPresets && interfaceId != 21578 && (interfaceId < 21579 || interfaceId > 21589)) {
			if (!c.getItems().playerHasItem(removeId, 1, removeSlot))
				return;
			PresetManager.getSingleton().addInventoryItem(c, removeId, c.playerItemsN[removeSlot]);
			return;
		}

		if (interfaceId >= 21579 && interfaceId <= 21589) {
			PresetManager.getSingleton().removeEquipmentItem(c, interfaceId);
			return;
		}

		if (c.getBank().withdraw(interfaceId, removeId, 1)) {
			return;
		}

		if (c.getTobContainer().handleContainerAction1(interfaceId, removeSlot)) {
			return;
		}

		switch (interfaceId) {
			case GroupIronmanBank.INTERFACE_ITEM_CONTAINER_ID:
				GroupIronmanBank.processContainerAction(c, action);
				break;
			case ItemSpawner.CONTAINER_ID:
				ItemSpawner.spawn(c, removeId, 1);
				break;
			case 33405:
				for (int crystal : FireOfExchangeBurnPrice.crystals) {
					if (removeId == crystal) {
						c.sendMessage("@red@You cannot exchange this item for Points.");
						return;
					}
				}

				int id = ItemDef.forId(removeId).isNoted() ? ItemDef.forId(removeId).getUnNotedIdIfNoted() : removeId;

				if (FireOfExchangeBurnPrice.getBurnPrice(c, id, true) != -1  && c.getItems().playerHasItem(removeId)) {//checks if item is on foe list, and they actually have item.
					c.getItems().sendItemContainer(33403, Lists.newArrayList(new GameItem(removeId, 1)));
					long price = FireOfExchangeBurnPrice.getBurnPrice(null, id, false);
					if (price == -1) {
						for (UpgradeMaterials value : UpgradeMaterials.values()) {
							if (value.getReward().getId() == id) {
								price = (int) (value.getCost() / 5);
							}
						}
					}
					int ironManPrice = (int) (price * 1.1);
					if (price == -1) {
						c.getPA().sendFrame126("@red@0", 33409);
						c.currentExchangeItem = -1;
					} else {
						if (c.getMode().isIronmanType() && FireOfExchange.canBurnWithBranch(c)) {
							price = ironManPrice;
						}
						c.getPA().sendFrame126("@gre@" + Misc.formatCoins(price), 33409);
						c.currentExchangeItem = removeId;
					}
					return;
				}

				break;


/*			case 26022:
				c.start(new DialogueBuilder(c).option("Are you sure you want to buy this item?",
						new DialogueOption("Yes.", p -> {
							Listing.buyListing(p, removeSlot, 1);
							p.getPA().closeAllWindows();
						}),
						new DialogueOption("No.", p -> p.getPA().closeAllWindows())));
			break;*/
			case 21578:
				PresetManager.getSingleton().removeInventoryItem(c, removeId);
				break;
			case 41609:
				switch(c.boxCurrentlyUsing) {
					case 12789://youtube
						c.getYoutubeMysteryBox().roll(c);
						break;
					case 13346: //ultra rare
						c.getUltraMysteryBox().roll(c);
						break;
					case 6831: //f2p box
						c.getF2pDivisionBox().roll(c);
						break;
					case 6829: //p2p box
						c.getP2pDivisionBox().roll(c);
						break;
					case 6199:
						c.getNormalMysteryBox().roll(c);
						break;
					case 6828:
						c.getSuperMysteryBox().roll(c);
						break;
					case 12161://christmas
						c.getChristmasBox().roll(c);
						break;
					case 8167:
						c.getFoeMysteryBox().roll(c);
						break;
					case 12579:
						c.getArboBox().roll(c);
						break;
					case 12582:
						c.getCoxBox().roll(c);
						break;
					case 12588:
						c.getDonoBox().roll(c);
						break;
					case 19891:
						c.getTobBox().roll(c);
						break;
					case 19897:
						c.getCosmeticBox().roll(c);
						break;
					case 6680:
						c.getMiniArboBox().roll(c);
						break;
					case 12585:
						c.getMiniCoxBox().roll(c);
						break;
					case 19887:
						c.getMiniDonoBox().roll(c);
						break;
					case 6679:
						c.getMiniNormalMysteryBox().roll(c);
						break;
					case 6677:
						c.getMiniSmb().roll(c);
						break;
					case 19895:
						c.getMiniTobBox().roll(c);
						break;
					case 6678:
						c.getMiniUltraBox().roll(c);
						break;
					case 28094:
						c.getBounty7().roll(c);
						break;
					case 33354:
						c.getWonderBox().roll(c);
						break;
					case 33353:
						c.getSupriseBox().roll(c);
						break;
					case 33355:
						c.getPhantomBox().roll(c);
						break;
					case 33356:
						c.getGreatPhantomBox().roll(c);
						break;
					case 33378:
						c.getSuperVoteBox().roll(c);
						break;
					case 33357:
						c.getBisBox().roll(c);
						break;
					case 33358:
						c.getChaoticBox().roll(c);
						break;
					case 33359:
						c.getCrusadeBox().roll(c);
						break;
					case 33391:
						c.getFreedomBox().roll(c);
						break;
					case 33360:
						c.getMiniShadowRaidBox().roll(c);
						break;
					case 33361:
						c.getShadowRaidBox().roll(c);
						break;
					case 33362:
						c.getHereditBox().roll(c);
						break;
					case 33374:
						c.getDamnedBox().roll(c);
						break;
					case 33364:
						c.getForsakenBox().roll(c);
						break;
					case 33377:
						c.getBoxes().roll(c);
						break;
					case 33381:
						c.getTumekensBox().roll(c);
						break;
					case 33375:
						c.getJudgesBox().roll(c);
						break;
					case 33376:
						c.getXamphurBox().roll(c);
						break;
					case 33363:
						c.getMinotaurBox().roll(c);
						break;

				}
				break;
		
/*		case 48847:
			Listing.cancelListing(c, removeSlot, removeId);
		break;*/
		
/*		case 48500: //Listing interface
			if(c.isListing) {
				Listing.openSelectedItem(c, removeId, 1, 0);
			}
		break;*/

			case 7423:
				if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
					Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					c.sendMessage("You cannot add items to the deposit box whilst trading.");
					return;
				}
				c.getItems().addToBank(removeId, 1, false);
				c.getItems().sendInventoryInterface(7423);
				break;
			case 4233:
			case 4239:
			case 4245:
				JewelryMaking.mouldItem(c, removeId, 1);
				break;

			case 65023:
				equip.removeGear(c, removeId, 0);
				break;
			case 65024:
				equip.removeGear(c, removeId, 2);
				break;
			case 65025:
				equip.removeGear(c, removeId, 1);
				break;
			case 65026:
				equip.removeGear(c, removeId, 4);
				break;
			case 65027:
				equip.removeGear(c, removeId, 7);
				break;
			case 65028:
				equip.removeGear(c, removeId, 10);
				break;
			case 65029:
				equip.removeGear(c, removeId, 3);
				break;
			case 65030:
				equip.removeGear(c, removeId, 5);
				break;
			case 65031:
				equip.removeGear(c, removeId, 13);
				break;
			case 65032:
				equip.removeGear(c, removeId, 12);
				break;
			case 65033:
				equip.removeGear(c, removeId, 9);
				break;

			// Remove equipment
			case 1688:
				if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {

					Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.TRADE).finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					c.sendMessage("You cannot remove items whilst trading, trade declined.");
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
				c.usingMagic = false;
				c.getItems().unequipItem(removeId, removeSlot);
				break;

			case 5064:
				if (Server.getMultiplayerSessionListener().inSession(c, MultiplayerSessionType.TRADE)) {
					Server.getMultiplayerSessionListener().finish(c, MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					c.sendMessage("You cannot add items to the bank whilst trading.");
					return;
				}
				duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(c, MultiplayerSessionType.DUEL);
				if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
						&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
					c.sendMessage("You have declined the duel.");
					duelSession.getOther(c).sendMessage("The challenger has declined the duel.");
					duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
					return;
				}
				if (c.isBanking) {
					c.getItems().addToBank(removeId, 1, true);
				} else {
					GroupIronmanBank.processContainerAction(c, action);
				}
				break;

			/**
			 * Shop value
			 */

			case 64016:
				c.getShops().buyFromShopPrice(removeId);
				break;

			case 3900:
				c.getShops().buyFromShopPrice(removeId);
				break;

			case 3823:
				c.getShops().sellToShopPrice(removeId);
				break;

			case 3322:
				MultiplayerSession session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
				if (Objects.isNull(session)) {
					return;
				}
				if (session instanceof TradeSession || session instanceof DuelSession || session instanceof FlowerPokerSession) {
					session.addItem(c, new GameItem(removeId, 1));
				}
				break;

			case 3415:
				session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
				if (Objects.isNull(session)) {
					return;
				}
				if (session instanceof TradeSession || session instanceof FlowerPokerSession) {
					session.removeItem(c, removeSlot, new GameItem(removeId, 1));
				}
				break;

			case 6669:
				session = Server.getMultiplayerSessionListener().getMultiplayerSession(c);
				if (Objects.isNull(session)) {
					return;
				}
				if (session instanceof DuelSession) {
					session.removeItem(c, removeSlot, new GameItem(removeId, 1));
				}
				break;

			case 1119:
			case 1120:
			case 1121:
			case 1122:
			case 1123:
			case 2031:
				Smithing.readInput(c.playerLevel[Player.playerSmithing], Integer.toString(removeId), c, 1);
				break;
			case 35150:
				c.getUpgradeInterface().handleItemAction(removeSlot);
				break;
			case 37237:
				c.getFusionSystem().handleItemAction(removeSlot);
				break;
			case 36150:
				c.getDeathInterface().retrieveSlotItem(removeSlot);
				break;
		}
	}

}
