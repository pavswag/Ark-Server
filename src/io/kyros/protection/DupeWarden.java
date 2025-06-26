package io.kyros.protection;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.model.SlottedItem;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.bank.BankItem;
import io.kyros.model.items.bank.BankTab;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.util.discord.Discord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/04/2024
 */
public class DupeWarden {

    private static final Logger logger = LoggerFactory.getLogger(DupeWarden.class);

    private static final int RAW_INC_THRESHOLD = 26_000_000; // 750k of alch value is a LOT.
    private static final int RAW_INC_CRITICAL = 50_000_000; // 5 million!? of alch value is a HUUUGE LOT.

    private static final int NOMAD_RAW_INC_THRESHOLD = 1_000_000; // 1 million Nomad value threshold for significant increase
    private static final int NOMAD_RAW_INC_CRITICAL = 10_000_000; // 10 million Nomad value threshold for critical increase

    private static final int[] specialItemChecker = {
            33237, 4087, 4585, 1149, 1187, 7980, 7981, 7979, 21275, 23077, 24466, 11157, 10933, 10939, 10940, 10941,
            13258, 13259, 13260, 13261, 5553, 5554, 5555, 5556, 5557, 13642, 13640, 13644, 13646, 12013, 12014, 12015,
            12016, 20704, 20706, 20708, 20710, 20517, 20520, 20595, 3694, 9032, 4722, 4720, 4716, 4718, 4714, 4712,
            4708, 4710, 4736, 4738, 4732, 4734, 4753, 4755, 4757, 4759, 4745, 4747, 4749, 4751, 4724, 4726, 4728,
            4730, 9030, 9042, 2951, 20366, 22249, 23444, 23240, 2577, 6737, 6733, 6731, 11907, 21892, 21895, 12603,
            12605, 11834, 13239, 13237, 13235, 12924, 12926, 9040, 9028, 2948, 20789, 22322, 21006, 22477, 23848,
            23842, 23845, 21018, 21021, 21024, 22326, 22327, 22328, 2950, 10330, 10332, 10334, 10336, 10338, 10340,
            10342, 10344, 10346, 10348, 10350, 10352, 12899, 26710, 26708, 22547, 22550, 22542, 22545, 22552, 22555,
            10556, 10557, 10558, 10559, 24417, 23995, 24419, 24420, 24421, 26714, 26715, 26716, 26221, 26223, 26225,
            26227, 26229, 18, 20786, 21129, 12006, 25066, 25063, 25059, 20997, 26482, 26486, 19553, 19547,
            19544, 22325, 22323, 11836, 12422, 12437, 12600, 12954, 26719, 26718, 26720, 33159, 11802, 11804, 11806,
            11808, 13576, 19481, 4151, 20784, 6585, 12002, 12004, 11924, 11926, 12806, 12807, 21028, 11920, 6739,
            24664, 24666, 24668, 691, 692, 693, 696, 8866, 8868, 33073, 33074, 33090, 33091, 33102, 33103, 33104,
            33105, 33106, 33107, 33108, 33117, 33118, 33119, 33121, 33079, 33080, 33087, 33088, 33089, 33093, 33094,
            33095, 33096, 33097, 33098, 33099, 33100, 33101, 33122, 33077, 33078, 33072, 33075, 33076, 33081, 33082,
            33083, 33084, 33085, 33086, 33092, 33114, 33115, 33116, 33120, 33123, 33124, 33162, 957, 787, 762, 609,
            608, 2397, 2404, 6770, 697, 694, 20998, 6806, 26383, 26385, 26387, 27227, 27230, 27233, 10944, 26375,
            33240, 23000, 11482, 26915, 25740, 25737, 27276, 33192, 27254, 27252, 28255, 28257, 28259, 33144, 27236,
            27239, 27242, 26226, 26222, 26224, 26552, 11740, 19898, 13347, 12586, 12583, 19888, 12589, 6830, 6832,
            6681, 12580, 19896, 19892, 8168, 2529, 22094, 23934, 13303, 27247, 27288, 33185, 30023, 4587, 20000,
            12774, 20368, 20372, 20374, 20370, 26712, 9185, 21012, 25916, 33206, 33058, 22324, 25734, 27246, 25979,
            27287, 33141, 33189, 28254, 28256, 28258, 25985, 27251, 28682, 21015, 8842, 24182, 24183, 13072, 24184,
            13073, 24185, 26463, 26465, 26469, 26471, 26467, 27226, 27229, 27232, 27235, 27238, 27241, 24664, 24666,
            24668, 33149, 33239
    };

    private long inv = -1;
    private long equip = -1;
    private long bank = -1;
    private long total = -1;
    private long invNomad = -1;
    private long equipNomad = -1;
    private long bankNomad = -1;
    private long totalNomad = -1;
    private int ticksUntil = 5;
    private boolean exclude;

    public void update(Player player) {
        if (player.getRights().isOrInherits(Right.ADMINISTRATOR))//Has spawn rights so we ignore them
            return;

        if (ticksUntil-- > 0) return;


        updateItemQuantitiesBeforeUpdate(player);
        checkForSuspiciousItemIncreases(player);

        long newinv = totalPriceOf(player);
        long newbank = totalPriceOfBank(player);
        long newequip = totalPriceOfEquipment(player);
        long newtotal = newinv + newbank + newequip;

        long newinvNomad = totalNomadValueOf(player);
        long newbankNomad = totalNomadValueOfBank(player);
        long newequipNomad = totalNomadValueOfEquipment(player);
        long newtotalNomad = newinvNomad + newbankNomad + newequipNomad;

        if (!exclude) {
            if (total != -1 && criticalIncrease(total, newtotal, false)) {
                reportCritical(player, newinv, newbank, newequip, newtotal, false);
            } else if (total != -1 && significantIncrease(total, newtotal, false)) {
                report(player, newinv, newbank, newequip, newtotal, false);
            }
        }

        if (!exclude) {
            if (totalNomad != -1 && criticalIncrease(totalNomad, newtotalNomad, true)) {
                reportCritical(player, newinvNomad, newbankNomad, newequipNomad, newtotalNomad, true);
            } else if (totalNomad != -1 && significantIncrease(totalNomad, newtotalNomad, true)) {
                report(player, newinvNomad, newbankNomad, newequipNomad, newtotalNomad, true);
            }
        }

        totalNomad = newtotalNomad;
        invNomad = newinvNomad;
        equipNomad = newequipNomad;
        bankNomad = newbankNomad;

        total = newtotal;
        inv = newinv;
        equip = newequip;
        bank = newbank;

        exclude = false;
        ticksUntil = 5;
    }

    public void exclude() {
        exclude = true;
    }

    private void report(Player player, long inv, long bank, long equip, long total, boolean isNomad) {
        if (isNomad) {
//            logger.warn("[Nomad] Dupe Warden spotted significant increase of Nomad wealth for {}. {}/{}/{} ({}) => {}/{}/{} ({}), increase of {}/{}/{} ({}).",
//                    player.getDisplayName(), this.invNomad, this.equipNomad, this.bankNomad, this.totalNomad,
//                    inv, equip, bank, total, signed(inv - this.invNomad), signed(equip - this.equipNomad),
//                    signed(bank - this.bankNomad), signed(total - this.totalNomad));
        } else {
//            logger.warn("Dupe Warden spotted significant increase of wealth for {}. {}/{}/{} ({}) => {}/{}/{} ({}), increase of {}/{}/{} ({}).",
//                    player.getDisplayName(), this.inv, this.equip, this.bank, this.total,
//                    inv, equip, bank, total, signed(inv - this.inv), signed(equip - this.equip), signed(bank - this.bank),
//                    signed(total - this.total));
        }
    }

    private void reportCritical(Player player, long inv, long bank, long equip, long total, boolean isNomad) {
        if (isNomad) {
//            logger.warn("[Nomad] Dupe Warden spotted CRITICAL increase of Nomad wealth for {}! {}/{}/{} ({}) => {}/{}/{} ({}), increase of {}/{}/{} ({}).",
//                    player.getDisplayName(), this.invNomad, this.equipNomad, this.bankNomad, this.totalNomad,
//                    inv, equip, bank, total, signed(inv - this.invNomad), signed(equip - this.equipNomad),
//                    signed(bank - this.bankNomad), signed(total - this.totalNomad));

            if (!Configuration.DISABLE_DUPE_WARDEN) {
                String time = calculatePlayTime(player);
                Discord.sendCriticalWarning(player, time, this.totalNomad, total, true);
            }
        } else {
//            logger.warn("Dupe Warden spotted CRITICAL increase of wealth for {}! {}/{}/{} ({}) => {}/{}/{} ({}), increase of {}/{}/{} ({}).",
//                    player.getDisplayName(), this.inv, this.equip, this.bank, this.total,
//                    inv, equip, bank, total, signed(inv - this.inv), signed(equip - this.equip),
//                    signed(bank - this.bank), signed(total - this.total));

//            if (!Configuration.DISABLE_DUPE_WARDEN) {
//                String time = calculatePlayTime(player);
//                Discord.sendCriticalWarning(player, time, this.total, total, false);
//            }
        }
    }

    private String calculatePlayTime(Player player) {
        long milliseconds = (long) player.playTime * 600; // Assuming playTime is in minutes
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toMillis(days));
        return days + " days, " + hours + " hrs";
    }

    private String signed(long num) {
        if (num < 0) {
            return NumberFormat.getInstance().format(num);
        } else {
            return "+" + NumberFormat.getInstance().format(num);
        }
    }

    private static long totalPriceOf(Player player) {
        long l = 0;
        for (SlottedItem inventoryItem : player.getItems().getInventoryItems()) {
            if (inventoryItem != null) {
                long amount =  ((long) ShopAssistant.getItemShopValue(inventoryItem.getId()));
                if (amount == 0) {
                    amount = 1;
                }
                l += amount;
            }
        }
        return l;
    }

    private static long totalPriceOfEquipment(Player player) {
        long l = 0;
        for (int playerEquipment : player.playerEquipment) {
            long amount = ((long) ShopAssistant.getItemShopValue(playerEquipment));
            if (amount == 0) {
                amount = 1;
            }

            l += amount;
        }
        return l;
    }

    private static long totalPriceOfBank(Player player) {
        long l = 0;
        for (BankTab tab : player.getBank().getBankTab()) {
            for (BankItem item : tab.getItems()) {
                int amount = ShopAssistant.getItemShopValue(item.getId());
                if (amount == 0) {
                    amount = 1;
                }
                l += amount;
            }
        }
        return l;
    }
    private static boolean significantIncrease(long old, long current, boolean isNomad) {
        if (isNomad) {
            return current - old > NOMAD_RAW_INC_THRESHOLD;
        } else {
            return current - old > RAW_INC_THRESHOLD;
        }
    }

    private static boolean criticalIncrease(long old, long current, boolean isNomad) {
        if (isNomad) {
            return current - old > NOMAD_RAW_INC_CRITICAL;
        } else {
            return current - old > RAW_INC_CRITICAL;
        }
    }

    private long totalNomadValueOf(Player player) {
        long total = 0;
        for (SlottedItem inventoryItem : player.getItems().getInventoryItems()) {
            if (inventoryItem != null) {
                total += FireOfExchangeBurnPrice.getBurnPrice(player, inventoryItem.getId(), false);
            }
        }

        total += player.foundryPoints;

        return total;
    }

    private long totalNomadValueOfEquipment(Player player) {
        long total = 0;
        for (int playerEquipment : player.playerEquipment) {
            total += FireOfExchangeBurnPrice.getBurnPrice(player, playerEquipment, false);
        }
        return total;
    }

    private long totalNomadValueOfBank(Player player) {
        long total = 0;
        for (BankTab tab : player.getBank().getBankTab()) {
            for (BankItem item : tab.getItems()) {
                total += FireOfExchangeBurnPrice.getBurnPrice(player, item.getId(), false);
            }
        }
        return total;
    }

    // Add a map to keep track of item quantities before update
    private Map<Integer, Integer> itemQuantitiesBeforeUpdate = new HashMap<>();

    // Add a method to update item quantities before each update
    private void updateItemQuantitiesBeforeUpdate(Player player) {
        itemQuantitiesBeforeUpdate.clear();
        // Assuming you have a method to get player's item quantities
        for (int itemId : specialItemChecker) {
            itemQuantitiesBeforeUpdate.put(itemId, getPlayerItemQuantity(player, itemId));
        }
    }

    private int getPlayerItemQuantity(Player player, int itemId) {
        int amount = 0;

        amount += player.getItems().getInventoryCount(itemId);

        amount += player.getBank().getItemCountInTabs(itemId);

        return amount;
    }


    private void checkForSuspiciousItemIncreases(Player player) {
        // Compare current item quantities with the quantities before update
        for (int itemId : specialItemChecker) {
            int quantityBefore = itemQuantitiesBeforeUpdate.getOrDefault(itemId, 0);
            int quantityAfter = getPlayerItemQuantity(player, itemId);
            int increase = quantityAfter - quantityBefore;

            // Define a threshold for maximum allowable increase
            int maxAllowableIncrease = getMaxAllowableIncrease(itemId);

            // Check if the increase exceeds the threshold
            if (increase > maxAllowableIncrease) {
                // Log a warning or take appropriate action
                for (Player player1 : Server.getPlayers().toPlayerArray()) {
                    if (player1 != null && player1.getRights().isOrInherits(Right.HELPER) && !player1.getDisplayName().equalsIgnoreCase("prophet")) {
                        player1.sendMessage("@red@Suspicious quantity increase for item {} for player {}. Increase: {}",
                                ItemDef.forId(itemId).getName(), player.getDisplayName(), increase);
                    }
                }
            }
        }
    }

    private int getMaxAllowableIncrease(int itemId) {
        long foe_price = FireOfExchangeBurnPrice.getBurnPrice(null, itemId, false);

        if (foe_price >= 100_000_000) {
            return 3;
        }

        if (foe_price >= 10_000_000 && foe_price <= 75_000_000) {
            return 4;
        }

        if (foe_price >= 1_000_000 && foe_price <= 8_000_000) {
            return 6;
        }
        switch (itemId+1) {
            case 957:
            case 787:
            case 762:
            case 609:
            case 608:
            case 2397:
            case 2404:
            case 6770:
                return 5;
            case 697:
            case 694:
            case 693:
            case 692:
                return 1000;
            case 22326:
            case 20998:
                return 5;
            case 6806:
                return 25;
            case 26383:
            case 26385:
            case 26387:
            case 27227:
            case 27230:
            case 27233:
            case 10944:
            case 26375:
            case 20787:
            case 33240:
            case 23000:
            case 11482:
            case 26915:
            case 25740:
            case 25737:
            case 27276:
            case 33190:
            case 33191:
            case 33192:
            case 27254:
            case 27252:
            case 28255:
            case 28257:
            case 28259:
            case 33142:
            case 33143:
            case 33144:
            case 27236:
            case 27239:
            case 27242:
            case 26226:
            case 26222:
            case 26224:
            case 26552:
                return 5;
            case 11740:
                return 250;
            case 19898:
                return 5;
            case 13347:
                return 250;
            case 12586:
            case 12583:
            case 19888:
            case 12589:
            case 6830:
            case 6832:
            case 6681:
            case 12580:
            case 19896:
            case 19892:
            case 8168:
                return 5;
            case 2529:
            case 22094:
            case 23934:
                return 100;
            case 13303:
                return 15;
            case 27247:
            case 27288:
            case 33185:
                return 2;
            case 30023:
                return 3;
        }


        return 20000;
    }
}
