package io.kyros.model.entity.player.packets.itemoptions;

import io.kyros.Server;
import io.kyros.content.combat.magic.SanguinestiStaff;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.dialogue.impl.AmethystChiselDialogue;
import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.item.lootable.newboxes.*;
import io.kyros.content.items.ChristmasWeapons;
import io.kyros.content.items.Degrade;
import io.kyros.content.items.Degrade.DegradableItem;
import io.kyros.content.items.PvpWeapons;
import io.kyros.content.items.TomeOfFire;
import io.kyros.content.items.pouch.RunePouch;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.skills.crafting.BryophytaStaff;
import io.kyros.content.teleportation.TeleportTablets;
import io.kyros.model.Items;
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

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Item Click 3 Or Alternative Item Option 1
 *
 * @author Ryan / Lmctruck30
 * <p>
 * Proper Streams
 */

public class ItemOptionThree implements PacketType {


    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        c.interruptActions();
        int itemId11 = c.getInStream().readSignedWordBigEndianA();
        int itemId1 = c.getInStream().readSignedWordA();
        int itemId = c.getInStream().readUnsignedWord();

        if (c.debugMessage) {
            c.sendMessage(String.format("ItemClick[item=%d, option=%d, interface=%d, slot=%d]", itemId, 3, -1, -1));
        }
        GameItem gameItem = new GameItem(itemId);
        ItemDef itemDef = ItemDef.forId(itemId);
        ItemAction action = null;
        ItemAction[] actions = itemDef.inventoryActions;
        if(actions != null)
            action = actions[2];
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
        if (SanguinestiStaff.clickItem(c, itemId, 3)) {
            return;
        }

        if (BryophytaStaff.handleItemOption(c, itemId, 3))
            return;
//        Server.pluginManager.triggerEvent(new io.kyros.script.event.impl.ItemAction(c, itemId, 3));

        switch (itemId) {
            case LootingBag.LOOTING_BAG:
            case LootingBag.LOOTING_BAG_OPEN:
                c.getDH().sendDialogues(LootingBag.OPTIONS_DIALOGUE_ID, 0);
                break;

            case 21183:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("@blu@You need at least 1 free slot to do this.");
                    return;
                }
                c.getItems().addItem(23824, c.slaughterCharge);
                c.sendMessage("You remove @red@" + c.slaughterCharge + " @bla@charges from your bracelet of slaughter.");
                c.slaughterCharge = 0;
                break;
            case 20714:
                c.getDH().sendDialogues(265, 2897);
                break;
            case 24271:
                if (!c.getItems().playerHasItem(24271)) {
                    c.sendMessage("@blu@You do not have the item to do this.");
                    return;
                }
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least two free slots to dismantle this item.");
                    return;
                }
                c.sendMessage("@blu@You have dismantled your helmet.");
                c.getItems().deleteItem(24271, 1);
                c.getItems().addItem(24268, 1);
                c.getItems().addItem(10828, 1);
                break;
            case 20716:
                TomeOfFire.store(c);
                break;
            case 24423:
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least two free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24423, 1))) {
                    return;
                }
                c.getItems().deleteItem(24423, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24511, 1);
                break;
            case 24424:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("You need at least one free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24424, 1))) {
                    return;
                }
                c.getItems().deleteItem(24424, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24514, 1);
                break;
            case 24425:
                if (c.getItems().freeSlots() < 1) {
                    c.sendMessage("You need at least one free slots to use this command.");
                    return;
                }
                if (!(c.getItems().playerHasItem(24425, 1))) {
                    return;
                }
                c.getItems().deleteItem(24425, 1);
                c.getItems().addItem(24422, 1);
                c.getItems().addItem(24517, 1);
                break;
            case Items.VIGGORAS_CHAINMACE: // Uncharging pvp weapons
            case Items.THAMMARONS_SCEPTRE:
            case Items.CRAWS_BOW:
                PvpWeapons.handleItemOption(c, itemId, 3);
                break;
            case 33160:
            case 33161:
            case 33162:
                ChristmasWeapons.handleItemOption(c, itemId, 3);
                break;

            case 12161:
                new ChristmasBox(c).quickOpen();
                break;
            case 22322:
                c.getDH().sendDialogues(333, 7456);
                break;
            case 1704:
                c.sendMessage("@red@You currently have no charges in your glory.");
                break;
            case 12932:
            case 12922:
            case 12929:
            case 12927:
            case 12924:
                String name = ItemDef.forId(itemId).getName();
                Consumer<Player> dismantle = plr -> {
                    plr.getPA().closeAllWindows();
                    if (!plr.getItems().playerHasItem(itemId))
                        return;
                    plr.getItems().deleteItem(itemId, 1);
                    plr.getItems().addItemUnderAnyCircumstance(Items.ZULRAHS_SCALES, 20_000);
                    plr.sendMessage("You dismantle the {} and receive 20,000 scales.", name);
                };

                new DialogueBuilder(c)
                        .itemStatement(itemId, "Are you sure you want to dismantle your " + name + "?", "You will receive 20,000 Zulrah scales.")
                        .option(new DialogueOption("Yes, dismantle it.", dismantle), DialogueOption.nevermind()).send();
                break;

            case 33363:
                new MinotaurBox(c).quickSpin();
                break;
            case 33376:
                new XamphurBox(c).quickSpin();
                break;
            case 33375:
                new JudgesBox(c).quickSpin();
                break;
            case 33381:
                new TumekensBox(c).quickSpin();
                break;
            case 33377:
                new Boxes(c).quickSpin();
                break;
            case 33364:
                new ForsakenBox(c).quickSpin();
                break;
            case 33374:
                new DamnedBox(c).quickSpin();
                break;
            case 33362:
                new HereditBox(c).quickSpin();
                break;
            case 33361:
                new ShadowRaidBox(c).quickSpin();
                break;
            case 33360:
                new MiniShadowRaidBox(c).quickSpin();
                break;
            case 33359:
                new CrusadeBox(c).quickSpin();
                break;
            case 33358:
                new ChaoticBox(c).quickSpin();
                break;
            case 33391:
                new FreedomBox(c).quickSpin();
                break;
            case 33357:
                new BisBox(c).quickSpin();
                break;
            case 33378:
                new SuperVoteBox(c).quickSpin();
                break;
            case 33355:
                new PhantomBox(c).quickSpin();
                break;
            case 33356:
                new GreatPhantomBox(c).quickSpin();
                break;
            case 33354:
                new WonderBox(c).quickSpin();
                break;
            case 33353:
                new Suprisebox(c).quickSpin();
                break;
            case 12579:
                new ArboBox(c).quickSpin();
                break;
            case 12582:
                new CoxBox(c).quickSpin();
                break;
            case 12588:
                new DonoBox(c).quickSpin();
                break;
            case 19891:
                new TobBox(c).quickSpin();
                break;
            case 19897:
                new CosmeticBox(c).quickOpen();
                break;
            case 6680:
                new MiniArboBox(c).quickSpin();
                break;
            case 12585:
                new MiniCoxBox(c).quickSpin();
                break;
            case 19887:
                new MiniDonoBox(c).quickSpin();
                break;
            case 6679:
                new MiniNormalMysteryBox(c).quickSpin();
                break;
            case 6677:
                new MiniSmb(c).quickSpin();
                break;
            case 19895:
                new MiniTobBox(c).quickSpin();
                break;
            case 6678:
                new MiniUltraBox(c).quickSpin();
                break;
            case 13346:
                new UltraMysteryBox(c).quickSpin();
                break;
            case 6829:
                new p2pDivisionBox(c).quickSpin();
                break;
            case 6831:
                new f2pDivisionBox(c).quickSpin();
                break;
            case 6199:
                new NormalMysteryBox(c).quickSpin();
                break;
            case 6828:
                new SuperMysteryBox(c).quickSpin();
                break;
            case 28094:
                new Bounty7(c).quickSpin();
                break;
            case 8167:
                new FoeMysteryBox(c).quickSpin();
                break;

            case 21347:
                c.start(new AmethystChiselDialogue(c));
                break;
            case 13125:
            case 13126:
            case 13127:
                if (c.getRunEnergy() < 100) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishRun(50);
                    }
                } else {
                    c.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13128:
                if (c.getRunEnergy() < 100) {
                    if (c.getRechargeItems().useItem(itemId)) {
                        c.getRechargeItems().replenishRun(100);
                    }
                } else {
                    c.sendMessage("You already have full run energy.");
                    return;
                }
                break;

            case 13226:
                c.getHerbSack().check();
                break;

            case 12020:
                c.getGemBag().withdrawAll();
                break;

            case 12902: //Toxic staff dismantle
                if (!c.getItems().playerHasItem(12902))
                    return;
                if (c.getItems().freeSlots() < 2)
                    return;

                c.getItems().deleteItem(12902, 1);
                c.getItems().addItem(12932, 1);
                c.getItems().addItem(11791, 1);
                c.sendMessage("You dismantle your toxic staff of the dead.");
                break;

            case 12900: //Toxic trident dismantle
                if (!c.getItems().playerHasItem(12900))
                    return;
                if (c.getItems().freeSlots() < 2)
                    return;

                c.getItems().deleteItem(12900, 1);
                c.getItems().addItem(12932, 1);
                c.getItems().addItem(11907, 1);
                c.sendMessage("You dismantle your toxic trident.");
                break;

            case 11283:
                if (c.getDragonfireShieldCharge() == 0) {
                    c.sendMessage("Your dragonfire shield has no charge.");
                    return;
                }
                c.setDragonfireShieldCharge(0);
                c.sendMessage("Your dragonfire shield has been emptied.");
                break;
            case 13196:
            case 13198:
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least 2 free slots for this.");
                    return;
                }
                c.getItems().deleteItem2(itemId, 1);
                c.getItems().addItem(12929, 1);
                c.getItems().addItem(itemId == 13196 ? 13200 : 13201, 1);
                c.sendMessage("You revoke the mutagen from the helmet.");
                break;
            case 11907:
            case 12899:
                int charge = itemId == 11907 ? c.getTridentCharge() : c.getToxicTridentCharge();
                if (charge <= 0) {
                    if (itemId == 12899) {
                        if (c.getToxicTridentCharge() == 0) {
                            if (c.getItems().freeSlots() > 1) {
                                c.getItems().deleteItem(12899, 1);
                                c.getItems().addItem(12932, 1);
                                c.getItems().addItem(11907, 1);
                                c.sendMessage("You dismantle your Trident of the swamp.");
                                return;
                            } else {
                                c.sendMessage("You need at least 2 inventory spaces to dismantle the trident.");
                                return;
                            }
                        }
                    } else {
                        c.sendMessage("Your trident currently has no charge.");
                        return;
                    }
                }

                if (c.getItems().freeSlots() < 3) {
                    c.sendMessage("You need at least 3 free slots for this.");
                    return;
                }
                c.getItems().addItem(554, 5 * charge);
                c.getItems().addItem(560, 1 * charge);
                c.getItems().addItem(562, 1 * charge);

                if (itemId == 12899) {
                    c.getItems().addItem(12934, 1 * charge);
                }

                if (itemId == 11907) {
                    c.setTridentCharge(0);
                } else {
                    c.setToxicTridentCharge(0);
                }
                c.sendMessage("You revoke " + charge + " charges from the trident.");
                break;
            case 28688:
            case 12926:
                if (c.getToxicBlowpipeAmmo() == 0 || c.getToxicBlowpipeAmmoAmount() == 0) {
                    c.sendMessage("You have no ammo in the pipe.");
                    return;
                }
                if (c.getItems().freeSlots() < 2) {
                    c.sendMessage("You need at least 2 free slots for this.");
                    return;
                }
                if (c.getItems().addItem(c.getToxicBlowpipeAmmo(), c.getToxicBlowpipeAmmoAmount())) {
                    c.setToxicBlowpipeAmmoAmount(0);
                    c.sendMessage("You unload the pipe.");
                }
                break;
            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566:
                //c.getPA().ROD();
                c.getPA().spellTeleport(3304, 3130, 0, false);
                break;
            case 11968:
            case 11970:
            case 11105:
            case 11107:
            case 11109:
            case 11111:
                c.getPA().handleSkills();
                c.isOperate = true;
                c.operateEquipmentItemId = itemId;
                break;
            case 1712:
            case 1710:
            case 1708:
            case 1706:
            case 19707:
                c.getPA().handleGlory();
                c.operateEquipmentItemId = itemId;
                c.isOperate = true;
                break;
            case 25910:
            case 24444:
            case 19639:
            case 19641:
            case 19643:
            case 19645:
            case 19647:
            case 19649:
            case 11864:
            case 11865:
                c.getSlayer().revertHelmet(itemId);
                break;

            case Items.COMPLETIONIST_CAPE:
            case 23859:
            case 13280:
            case 13329:
            case 13337:
            case 21898:
            case 13331:
            case 13333:
            case 13335:
            case 20760:
            case 21285:
            case 21776:
            case 21778:
            case 21780:
            case 21782:
            case 21784:
            case 21786:
            case 28902:
            case 27363:
                if (Server.getMultiplayerSessionListener().inAnySession(c)) {
                    return;
                }

                c.getPA().openQuestInterface("@dre@Max Cape Features",
                        "While wielding the cape you will:",
                        "Have a chance of saving ammo.",
                        "Deplete run energy slower.",
                        "Get more herbs & faster growth time.",
                        "Have less chance of burning food",
                        "Have 20% of saving a bar while smithing.",
                        "Have 20% of saving a herb while mixing potions.",
                        "Regenerate 2 hitpoints instead of 1 at a time.",
                        "Get 5+ restore per prayer/super restore sip.",
                        "Get double seeds while thieving the master farmer.",
                        "Be able to operate for additional options."
                );
                break;

            default:
                if (c.getRights().isOrInherits(Right.GAME_DEVELOPER)) {
                    Misc.println("[DEBUG] Item Option #3-> Item id: " + itemId);
                }
                break;
        }

    }

}
