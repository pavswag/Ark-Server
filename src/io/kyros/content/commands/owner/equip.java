package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.cache.definitions.identifiers.NumberUtils;
import io.kyros.content.combat.formula.rework.MeleeCombatFormula;
import io.kyros.content.combat.formula.rework.RangeCombatFormula;
import io.kyros.content.combat.weapon.WeaponDataConstants;
import io.kyros.content.commands.Command;
import io.kyros.model.Bonus;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;

import java.util.Objects;

public class equip extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        getStats(player);

        getGear(player);

        player.getPA().showInterface(65000);
    }

    private static final int[] range_weapons = {19481, 26712, 19478, 12788, 9185, 11785, 21012, 26374, 33206, 26269, 33435, 839, 845, 847, 851, 855, 859, 841, 843, 849,
            853, 857, 12424, 861, 4212, 4214, 4215, 12765, 12766, 12767, 12768, 11235, 4216, 4217, 4218, 4219, 4220, 29599,
            4221, 4222, 4223, 4734, 6724, 20997, 21902, 22550, 25916, 25918, 26486, 25865, 25867, 25884, 25888, 33058, 33207, 25890, 25892, 25894, 25896,27655, 29599, 33434,
            //corrupted bow
            23855, 23857, 23902, 8880,
            11959, 10033, 10034, 800, 801, 802, 803, 804, 805, 20849, 806, 807, 808,
            809, 810, 811, 812, 813, 814, 815, 816, 817, 825, 826, 827, 828, 829, 830, 831, 832, 833, 834, 835, 836,
            863, 864, 865, 866, 867, 868, 869, 870, 871, 872, 873, 874, 875, 876, 4934, 4935, 4936, 4937, 5628, 5629,
            5630, 5632, 5633, 5634, 5635, 5636, 5637, 5639, 5640, 5641, 5642, 5643, 5644, 5645, 5646, 5647, 5648, 5649,
            5650, 5651, 5652, 5653, 5654, 5655, 5656, 5657, 5658, 5659, 5660, 5661, 5662, 5663, 5664, 5665, 5666, 5667,
            6522, 11230, 11231, 11233,11234, 22804, 22634, 21318, 19484, 22636,
            11235, 12765 , 12766, 12767, 12768, 29599,
            10033, 28919, 28922,
            10034,
            11959};
    private static int[] magic_weapons = {123,123};

    protected static int maximumDamage = 0;

    public static void getStats(Player c) {
//        c.getPA().itemOnInterface(773,1, 65022, 0);
        c.getPA().sendString(65036, c.getDisplayName());
        c.getPA().sendString(65037, "Total Level: "+String.valueOf(c.getPA().calculateTotalLevel()));

        double specialDamageBoost = 1.0;
        double specialPassiveMultiplier = 1.0;
        //attack
        c.getPA().sendString(65005, String.valueOf((double) c.getBonus(Bonus.ATTACK_STAB)));
        c.getPA().sendString(65006, String.valueOf((double) c.getBonus(Bonus.ATTACK_SLASH)));
        c.getPA().sendString(65007, String.valueOf((double) c.getBonus(Bonus.ATTACK_CRUSH)));
        c.getPA().sendString(65008, String.valueOf((double) c.getBonus(Bonus.ATTACK_MAGIC)));
        c.getPA().sendString(65009, String.valueOf((double) c.getBonus(Bonus.ATTACK_RANGED)));
        //defense
        c.getPA().sendString(65010, String.valueOf((double) c.getBonus(Bonus.DEFENCE_STAB)));
        c.getPA().sendString(65011, String.valueOf((double) c.getBonus(Bonus.DEFENCE_SLASH)));
        c.getPA().sendString(65012, String.valueOf((double) c.getBonus(Bonus.DEFENCE_CRUSH)));
        c.getPA().sendString(65013, String.valueOf((double) c.getBonus(Bonus.DEFENCE_MAGIC)));
        c.getPA().sendString(65014, String.valueOf((double) c.getBonus(Bonus.DEFENCE_RANGED)));
        //other
        c.getPA().sendString(65015, String.valueOf((double) c.getBonus(Bonus.STRENGTH)));
        c.getPA().sendString(65016, String.valueOf((double) c.getBonus(Bonus.RANGED_STRENGTH)));
        c.getPA().sendString(65017, String.valueOf((double) c.getBonus(Bonus.MAGIC_DMG)));
        c.getPA().sendString(65018, String.valueOf((double) c.getBonus(Bonus.PRAYER)));

        int slayer = 0;

        for (int slayer_helmet : c.SLAYER_HELMETS) {
            if (c.getItems().isWearingItem(slayer_helmet)) {
                slayer += 15;
                break;
            }
        }

        int undead = 0;

        if (c.getItems().isWearingItem(12018)) {
            undead += 20;
        }
        //target-specific
        c.getPA().sendString(65019, String.valueOf(undead) + "%"); // undead
        c.getPA().sendString(65020, String.valueOf(slayer) + "%"); // slayer

        boolean range = false;
        boolean magic = false;

        for (int range_weapon : range_weapons) {
            if (c.playerEquipment[Player.playerWeapon] == range_weapon) {
                maximumDamage = RangeCombatFormula.STANDARD.getMaxHit(c, NPCHandler.getNpc(7413), specialDamageBoost,
                        specialPassiveMultiplier);
                range = true;
                c.getPA().sendString(65034, String.valueOf((double) maximumDamage));//MAX Hit
                break;
            }
        }

        for (int standardStaff : WeaponDataConstants.STANDARD_STAFFS) {
            if (standardStaff == ItemDef.forId(c.playerEquipment[Player.playerWeapon]).getId()) {
                c.getPA().sendString(65034, "N/A");//MAX Hit
                magic = true;
                break;
            }
        }

        for (int i : WeaponDataConstants.SOTD) {
            if (i == ItemDef.forId(c.playerEquipment[Player.playerWeapon]).getId()) {
                c.getPA().sendString(65034, "N/A");//MAX Hit
                magic = true;
                break;
            }
        }

        if (!range && !magic) {
            maximumDamage = MeleeCombatFormula.get().getMaxHit(c, NPCHandler.getNpc(7413), specialDamageBoost, specialPassiveMultiplier);
            c.getPA().sendString(65034, String.valueOf((double) maximumDamage));//MAX Hit
        }


        c.getPA().sendString(65035, NumberUtils.formatOnePlace(DropManager.getModifier1(c)));//Drop Rate
    }

    public static void getGear(Player c) {
        if (c.playerEquipment[Player.playerHat] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerHat], 1, 65023,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65023,0);
        }

        if (c.playerEquipment[Player.playerAmulet] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerAmulet], 1, 65024,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65024,0);
        }

        if (c.playerEquipment[Player.playerCape] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerCape], 1, 65025,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65025,0);
        }

        if (c.playerEquipment[Player.playerChest] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerChest], 1, 65026,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65026,0);
        }

        if (c.playerEquipment[Player.playerLegs] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerLegs], 1, 65027,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65027,0);
        }

        if (c.getEquipmentToShow(Player.playerFeet) > 0) {
            c.getPA().itemOnInterface(c.getEquipmentToShow(Player.playerFeet), 1, 65028,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65028,0);
        }

        if (c.playerEquipment[Player.playerWeapon] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerWeapon], 1, 65029,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65029,0);
        }

        if (c.playerEquipment[Player.playerShield] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerShield], 1, 65030,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65030,0);
        }
        if (c.playerEquipment[Player.playerArrows] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerArrows], c.playerEquipmentN[Player.playerArrows], 65031,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65031,0);
        }
        if (c.playerEquipment[Player.playerRing] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerRing], 1, 65032,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65032,0);
        }
        if (c.playerEquipment[Player.playerHands] > 0) {
            c.getPA().itemOnInterface(c.playerEquipment[Player.playerHands], 1, 65033,0);
        } else {
            c.getPA().itemOnInterface(new GameItem(-1, 1),65033,0);
        }
    }

    public static void removeGear(Player c, int wearID, int slot) {
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
        if (Boundary.isIn(c, Boundary.WG_Boundary) && slot == Player.playerWeapon) {
            c.sendMessage("You can't unequip your weapon inside of here!");
            return;
        }
        c.usingMagic = false;
        c.getItems().unequipItem(wearID, slot);
    }
}