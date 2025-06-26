package io.kyros.model.entity.player.packets;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.cache.definitions.NpcDefinition;
import io.kyros.content.combat.magic.SanguinestiStaff;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.items.ItemCombinations;
import io.kyros.content.miniquests.magearenaii.MageArenaII;
import io.kyros.content.pet.Pet;
import io.kyros.content.pet.PetManager;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.model.Items;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.group.GroupIronmanRepository;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemCombination;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.logging.player.ItemDroppedLog;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;

import java.util.Objects;
import java.util.Optional;

/**
 * Drop Item Class
 **/
public class DropItem implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		player.interruptActions();
		int itemId = player.getInStream().readUnsignedWordA();
		player.getInStream().readUnsignedByte();
		player.getInStream().readUnsignedByte();
		int slot = player.getInStream().readUnsignedWordA();

		if (player.debugMessage) {
			player.sendMessage(String.format("DropItem[item=%d, slot=%d]", itemId, slot));
		}

		if (player.tradeBanned) {
			player.sendErrorMessage("You're currently trade banned.");
			return;
		}

		if (!player.getItems().isItemInInventorySlot(itemId, slot))
			return;

		if (player.getPA().viewingOtherBank) {
			player.getPA().resetOtherBank();
		}
		if (!player.getItems().playerHasItem(itemId)) {
			return;
		}
		if (player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen() || player.viewingRunePouch) {
			return;
		}
		if (player.isStuck) {
			player.isStuck = false;
			player.sendMessage("@red@You've disrupted stuck command, you will no longer be moved home.");
			return;
		}
		if (player.isNpc) {
			return;
		}
		if ((Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY) || Boundary.isIn(player, Boundary.OUTLAST_LOBBY)) &&
				(itemId != 13341 || itemId != 11936 || itemId != 3144 || itemId != 385 )) {
			player.getItems().deleteItem2(itemId, 1);
			return;
		}
		if (itemId == Items.SANGUINESTI_STAFF) {
			SanguinestiStaff.clickItem(player, itemId, 5);
			return;
		}
		if (Boundary.isIn(player, Boundary.OUTLAST_HUT)) {
			player.sendMessage("Please leave the outlast hut area to drop your items.");
			return;
		}
		if (itemId == 9699) {
			player.getItems().deleteItem2(9699, 1);
			return;
		}
		if (itemId == 9698) {
			player.getItems().deleteItem2(9698, 1);
			return;
		}
		if (itemId == 9017) {
			player.getItems().deleteItem2(9017, 1);
			return;
		}
		if (itemId == 23783) {
			player.getItems().deleteItem2(23783, 1);
			return;
		}
		if (itemId == 23778) {
			player.getItems().deleteItem2(23778, 1);
			return;
		}

		ItemDef itemDef = ItemDef.forId(itemId);

		if (itemDef.isDestroyable() || MageArenaII.isUntradable(itemId)) {
			player.getPA().destroyInterface(new ItemToDestroy(itemId, slot, DestroyType.DESTROY));
			return;
		}

		if (!itemDef.isDroppable()) {
			player.sendMessage("You can't drop this item!");
			return;
		}

		if (!Boundary.isIn(player, Boundary.OUTLAST_AREA)
				|| !Boundary.isIn(player, Boundary.FOREST_OUTLAST)
				|| !Boundary.isIn(player, Boundary.SNOW_OUTLAST)
				|| !Boundary.isIn(player, Boundary.ROCK_OUTLAST)
				|| !Boundary.isIn(player, Boundary.FALLY_OUTLAST)
				|| !Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST)
				|| !Boundary.isIn(player, Boundary.SWAMP_OUTLAST)
				|| Boundary.isIn(player, Boundary.WG_Boundary)) {
			int amount = player.getItems().getItemAmount(itemId);
			ItemDef def = itemDef;
			Discord.writeDropHandler("[Drop-Log]" + player.getDisplayName() + " dropped " + def.getName() + " x " + Misc.insertCommas(amount) + " at " + player.absX + ", " + player.absY);
		}

		if (itemDef.isCheckBeforeDrop()) {
			player.destroyingItemId = itemId;
			player.getDH().sendDialogues(858, 7456);
			return;
		}
		if (player.getBankPin().requiresUnlock()) {
			player.getBankPin().open(2);
			return;
		}
			for (int item : Configuration.TOURNAMENT_ITEMS_DROPPABLE) {
				if ((Boundary.isIn(player, Boundary.OUTLAST_AREA)  || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)
						|| Boundary.isIn(player, Boundary.FOREST_OUTLAST)
						|| Boundary.isIn(player, Boundary.SNOW_OUTLAST)
						|| Boundary.isIn(player, Boundary.ROCK_OUTLAST)
						
						|| Boundary.isIn(player, Boundary.FALLY_OUTLAST)
						|| Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST)
						|| Boundary.isIn(player, Boundary.SWAMP_OUTLAST)
						|| Boundary.isIn(player, Boundary.WG_Boundary)) && (item == itemId)) {
					player.getItems().deleteItem(itemId, 1);
					player.sendMessage("Your food dissapears as it hits the floor..");
					return;
				}
			}
			if (player.itemId == 5509 || player.itemId == 5510 || player.itemId == 5512 || player.itemId == 5514 || player.itemId == 6819 ||
					player.itemId == 13199 || player.itemId == 12931 || player.itemId == 13197) {
					player.getDH().sendDialogues(858, 7456);
					return;
				}
		if (player.getInterfaceEvent().isActive()) {
			player.sendMessage("Please finish what you're doing.");
			return;
		}
		PetHandler.Pets pet = PetHandler.forItem(itemId);

		if(pet != null) {
			if(player.hasPet(pet.npcId)) {
				player.sendMessage("You have already unlocked this pet!");
			} else {
				player.start(new DialogueBuilder(player).statement("Unlocking a pet will remove the item. Do you wish to continue?").option(
						new DialogueOption("Yes", p -> {

							StringBuilder statement = new StringBuilder().append("Unlocking Pet.");
							CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
								@Override
								public void execute(CycleEventContainer container) {
									if (container.getTotalExecutions() == 1) {
										statement.append(".");
									}
									if(container.getTotalExecutions() == 2) {
										statement.append(".");
									}
									p.start(new DialogueBuilder(p).statement(statement.toString()));
									if (container.getTotalExecutions() == 4) {
										if(p.itemAssistant.getInventoryCount(itemId) > 0) {
											p.itemAssistant.deleteItem(itemId, 1);
											Pet newPet = new Pet(pet.npcId);
											newPet.addDefaultPerks();
											p.getPetCollection().add(newPet);
											PetManager.updateInterface(p);
											p.sendMessage("You have successfully unlocked the " + Server.definitionRepository.get(NpcDefinition.class, pet.npcId).name + " pet!");
											p.sendMessage("You may now view it in ::pet");
											p.getPA().closeAllWindows();
										}
										container.stop();
									}
								}
							}, 1);
						}),
						new DialogueOption("No", p -> {
							p.getPA().closeAllWindows();
						})
				));
			}
			return;
		}




		if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
			player.sendMessage("You can't drop items inside the arena!");
			return;
		}

		if (player.playerAttackingIndex > 0 && !TourneyManager.getSingleton().isInArena(player)) {
			player.sendMessage("You can't drop items during player combat.");
			return;
		}
		if (player.inTrade) {
			player.sendMessage("You can't drop items while trading!");
			return;
		}
		DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player,
				MultiplayerSessionType.DUEL);
		if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST
				&& duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
			player.sendMessage("Your actions have declined the duel.");
			duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
			duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
			return;
		}

		if (slot >= player.playerItems.length || slot < 0 || slot >= player.playerItems.length) {
			return;
		}

		switch (itemId) {
		case 19722:
			player.getItems().deleteItem(19722, 1);
			player.getItems().addItem(12954, 1);
			player.getItems().addItem(20143, 1);
			player.sendMessage("Your trimmed dragon defender has turned into regular again.");
			break;
		}

		if (itemId == 12904) {
			if (player.getToxicStaffOfTheDeadCharge() <= 0) {
				player.getItems().deleteItem2(12904, 1);
				player.getItems().addItem(12902, 1);
				player.sendMessage("The staff had no charge, but has been reverted to uncharged.");
				return;
			}
			if (player.getItems().freeSlots() <= 0) {
				player.sendMessage("You need one free slot to do this.");
				return;
			}
			player.getItems().deleteItem2(12904, 1);
			player.getItems().addItem(12902, 1);
			player.getItems().addItem(12934, player.getToxicStaffOfTheDeadCharge());
			player.setToxicStaffOfTheDeadCharge(0);
			player.sendMessage("You uncharged the toxic staff of the dead and retain.");
		}

		if (itemId == 12926 || itemId == 28688) {
			int ammo = player.getToxicBlowpipeAmmo();
			int amount = player.getToxicBlowpipeAmmoAmount();
			int charge = player.getToxicBlowpipeCharge();
			if (ammo > 0 && amount > 0) {
				player.sendMessage("You must unload before you can uncharge.");
				return;
			}

			player.sendMessage("ammo: " + ammo);
			player.sendMessage("ammo amount: " + amount);
			player.sendMessage("charge: " + charge);
			if (charge <= 0) {
				player.sendMessage("The toxic blowpipe had no charge, it is emptied.");
				player.getItems().deleteItem2(itemId, 1);
				player.getItems().addItem(itemId == 28688 ? 28687 : 12924, 1);
				return;
			}
			if (player.getItems().freeSlots() < 2) {
				player.sendMessage("You need at least two free slots to do this.");
				return;
			}
			player.getItems().deleteItem2(itemId, 1);
			player.getItems().addItem(itemId == 28688 ? 28687 : 12924, 1);
			player.getItems().addItem(12934, charge);
			player.setToxicBlowpipeAmmo(0);
			player.setToxicBlowpipeAmmoAmount(0);
			player.setToxicBlowpipeCharge(0);
			return;
		}

		if (itemId == 12931 || itemId == 13199 || itemId == 13197) {
			int uncharged = itemId == 12931 ? 12929 : itemId == 13199 ? 13198 : 13196;
			int charge = player.getSerpentineHelmCharge();
			if (charge <= 0) {
				player.sendMessage("The serpentine helm had no charge, it is emptied.");
				player.getItems().deleteItem2(itemId, 1);
				player.getItems().addItem(uncharged, 1);
				return;
			}
			if (player.getItems().freeSlots() < 2) {
				player.sendMessage("You need at least two free slots to do this.");
				return;
			}
			player.getItems().deleteItem2(itemId, 1);
			player.getItems().addItem(uncharged, 1);
			player.getItems().addItem(12934, charge);
			player.setSerpentineHelmCharge(0);
			return;
		}

		Optional<ItemCombination> revertableItem = ItemCombinations.isRevertable(new GameItem(itemId));

		if (revertableItem.isPresent()) {
			// revertableItem.get().sendRevertConfirmation(c);
			revertableItem.get().revert(player);
			player.dialogueAction = 555;
			player.nextChat = -1;
			return;
		}

		if (!itemDef.isDroppable())
			return;

		if (player.underAttackByPlayer > 0) {
			if (ShopAssistant.getItemShopValue(itemId) > 1000000 && !Boundary.isIn(player, Boundary.OUTLAST_AREA)) {
				player.sendMessage("You may not drop items worth more than 1000000 while in combat.");
				return;
			}
		}

		if (player.showDropWarning()) {
			player.destroyItem = new ItemToDestroy(itemId, slot, DestroyType.DROP);
			player.getPA().destroyInterface("drop");
			return;
		}

		dropItem(player, itemId, slot);
	}

	public static void dropItem(Player c, int itemId, int itemSlot) {
		if (!c.getItems().isItemInInventorySlot(itemId, itemSlot) || c.isDead)
			return;
		if (c.jailEnd > 0)
			return;
		if (Boundary.isIn(c, Boundary.WG_Boundary)) {
			return;
		}

		Server.getLogging().write(new ItemDroppedLog(c, new GameItem(itemId, c.playerItemsN[itemSlot]), c.getPosition()));
		Server.itemHandler.createGroundItemFromDrop(c, itemId, c.absX, c.absY, c.heightLevel,
				c.playerItemsN[itemSlot], c.getIndex());
		c.getItems().deleteItem(itemId, itemSlot, c.playerItemsN[itemSlot]);
		c.getPA().removeAllWindows();
		c.getPA().sendSound(376);

		// Gim drop log
		GroupIronmanRepository.getGroupForOnline(c).ifPresent(group -> group.addDropItemLog(c, new GameItem(itemId, itemSlot)));
	}
}
