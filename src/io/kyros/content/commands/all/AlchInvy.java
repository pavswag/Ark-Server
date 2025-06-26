package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.items.pouch.RunePouch;
import io.kyros.content.lootbag.LootingBag;
import io.kyros.content.skills.crafting.BryophytaStaff;
import io.kyros.model.Graphic;
import io.kyros.model.Items;
import io.kyros.model.SoundType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.EquipmentSet;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.util.Misc;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AlchInvy extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.getCurrentPet().hasPerk("legendary_gold_whore") && c.getCurrentPet().findPetPerk("legendary_gold_whore").getValue() > 0D) {
            AtomicInteger totalGains = new AtomicInteger();
            AtomicLong totalCoins = new AtomicLong(c.getItems().getInventoryCount(995)); // Start with current coins using inventory count
            AtomicInteger totalPlatinumTokens = new AtomicInteger(c.getItems().getInventoryCount(Items.PLATINUM_TOKEN));

            c.start(new DialogueBuilder(c).option("Are you sure you want to alch your entire inventory?",
                    new DialogueOption("Yes", p -> {
                        p.getPA().closeAllWindows();

                        c.getItems().getInventoryItems().forEach(item -> {
                            int item_amount = item.getAmount();
                            int itemId = item.getDef().getUnNotedIdIfNoted();

                            // Skip items that cannot be alched
                            if (isItemNonAlchable(c, itemId)) return;

                            // Calculate the total amount the player would gain
                            long amount = (long) (ShopAssistant.getItemShopValue(itemId) * 0.75) * item_amount;

                            // Apply bonuses
                            if (BryophytaStaff.isWearingStaffWithCharge(c) && c.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                                amount *= 1.25;
                            }
                            amount += (long) ((amount / 100) * c.getCurrentPet().findPetPerk("legendary_gold_whore").getValue());

                            // Check if player can hold the coins without exceeding the max limit
                            long currentCoins = totalCoins.get();
                            int currentTokens = totalPlatinumTokens.get();

                            // If the player is about to exceed Integer.MAX_VALUE for coins
                            if (currentCoins + amount > Integer.MAX_VALUE) {
                                long excessCoins = currentCoins + amount - Integer.MAX_VALUE;
                                int tokensToAdd = (int) (excessCoins / 1000);

                                // Check if adding these tokens would exceed the max token capacity
                                if ((currentTokens + tokensToAdd) > Integer.MAX_VALUE) {
                                    c.sendMessage("You cannot hold more platinum tokens. Alching stopped to avoid overflow.");
                                    return;
                                }

                                // Add platinum tokens
                                totalPlatinumTokens.addAndGet(tokensToAdd);
                                c.getItems().addItem(Items.PLATINUM_TOKEN, tokensToAdd);

                                // Adjust the remaining amount after converting to tokens
                                amount -= tokensToAdd * 1000;
                            }

                            // Now that the checks are done, delete the item and add the remaining coins
                            c.getItems().deleteItem(item.getId(), item_amount);
                            totalCoins.addAndGet(amount);
                            c.getItems().addItem(995, (int) amount); // Adding the remaining coins

                            totalGains.addAndGet((int) amount);
                        });

                        // Perform the alch animation and graphics just once after all items are processed
                        c.startAnimation(10624);
                        c.startGraphic(new Graphic(2609));
                        c.alchDelay = System.currentTimeMillis();
                        c.getPA().forceOpenTab(3);
                        c.getPA().sendSound(97, SoundType.SOUND);

                        // Inform the player of total gold gained and platinum tokens received
                        c.sendMessage("You have received a total of " + Misc.formatCoins(totalGains.get()) + " gold from alching your inventory.");
                        if (totalPlatinumTokens.get() > 0) {
                            c.sendMessage("You also received " + totalPlatinumTokens.get() + " platinum tokens.");
                        }
                    }),
                    new DialogueOption("No", p -> p.getPA().closeAllWindows())
            ));
        } else if (c.getCurrentPet().hasPerk("common_philos_stone") && c.getCurrentPet().findPetPerk("common_philos_stone").getValue() > 0D) {
            AtomicInteger totalGains = new AtomicInteger();
            AtomicLong totalCoins = new AtomicLong(c.getItems().getInventoryCount(995)); // Start with current coins using inventory count
            AtomicInteger totalPlatinumTokens = new AtomicInteger(c.getItems().getInventoryCount(Items.PLATINUM_TOKEN));

            c.start(new DialogueBuilder(c).option("Are you sure you want to alch your entire inventory?",
                    new DialogueOption("Yes", p -> {
                        p.getPA().closeAllWindows();

                        c.getItems().getInventoryItems().forEach(item -> {
                            int item_amount = item.getAmount();
                            int itemId = item.getDef().getUnNotedIdIfNoted();

                            // Skip items that cannot be alched
                            if (isItemNonAlchable(c, itemId)) return;

                            // Calculate the total amount the player would gain
                            long amount = (long) (ShopAssistant.getItemShopValue(itemId) * 0.75) * item_amount;

                            // Apply bonuses
                            if (BryophytaStaff.isWearingStaffWithCharge(c) && c.getItems().isWearingItem(Items.TOME_OF_FIRE, Player.playerShield)) {
                                amount *= 1.25;
                            }
                            amount += (long) ((amount / 100) * c.getCurrentPet().findPetPerk("common_philos_stone").getValue());

                            // Check if player can hold the coins without exceeding the max limit
                            long currentCoins = totalCoins.get();
                            int currentTokens = totalPlatinumTokens.get();

                            // If the player is about to exceed Integer.MAX_VALUE for coins
                            if (currentCoins + amount > Integer.MAX_VALUE) {
                                long excessCoins = currentCoins + amount - Integer.MAX_VALUE;
                                int tokensToAdd = (int) (excessCoins / 1000);

                                // Check if adding these tokens would exceed the max token capacity
                                if ((currentTokens + tokensToAdd) > Integer.MAX_VALUE) {
                                    c.sendMessage("You cannot hold more platinum tokens. Alching stopped to avoid overflow.");
                                    return;
                                }

                                // Add platinum tokens
                                totalPlatinumTokens.addAndGet(tokensToAdd);
                                c.getItems().addItem(Items.PLATINUM_TOKEN, tokensToAdd);

                                // Adjust the remaining amount after converting to tokens
                                amount -= tokensToAdd * 1000;
                            }

                            // Now that the checks are done, delete the item and add the remaining coins
                            c.getItems().deleteItem(item.getId(), item_amount);
                            totalCoins.addAndGet(amount);
                            c.getItems().addItem(995, (int) amount); // Adding the remaining coins

                            totalGains.addAndGet((int) amount);
                        });

                        // Perform the alch animation and graphics just once after all items are processed
                        c.startAnimation(10624);
                        c.startGraphic(new Graphic(2609));
                        c.alchDelay = System.currentTimeMillis();
                        c.getPA().forceOpenTab(3);
                        c.getPA().sendSound(97, SoundType.SOUND);

                        // Inform the player of total gold gained and platinum tokens received
                        c.sendMessage("You have received a total of " + Misc.formatCoins(totalGains.get()) + " gold from alching your inventory.");
                        if (totalPlatinumTokens.get() > 0) {
                            c.sendMessage("You also received " + totalPlatinumTokens.get() + " platinum tokens.");
                        }
                    }),
                    new DialogueOption("No", p -> p.getPA().closeAllWindows())
            ));
        } else {
            c.sendMessage("You need the Gold Whore pet perk to use this command");
        }
    }

    // Helper method to check if the item cannot be alched
    private boolean isItemNonAlchable(Player c, int itemId) {
        for (int[] items : EquipmentSet.IRON_MAN_ARMOUR.getEquipment()) {
            if (Misc.linearSearch(items, itemId) > -1) {
                c.sendMessage("You cannot alch iron man armor.");
                return true;
            }
        }
        for (int[] items : EquipmentSet.ULTIMATE_IRON_MAN_ARMOUR.getEquipment()) {
            if (Misc.linearSearch(items, itemId) > -1) {
                c.sendMessage("You cannot alch ultimate iron man armor.");
                return true;
            }
        }
        for (int[] items : EquipmentSet.HC_MAN_ARMOUR.getEquipment()) {
            if (Misc.linearSearch(items, itemId) > -1) {
                c.sendMessage("You cannot alch hardcore iron man armor.");
                return true;
            }
        }
        if (itemId == LootingBag.LOOTING_BAG || itemId == LootingBag.LOOTING_BAG_OPEN || itemId == RunePouch.RUNE_POUCH_ID) {
            c.sendMessage("This kind of sorcery cannot happen.");
            return true;
        }
        return false;
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Alchs entire inventory");
    }
}
