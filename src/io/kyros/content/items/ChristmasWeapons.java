package io.kyros.content.items;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;

import java.util.Arrays;

public class ChristmasWeapons {

    private static final int MAX_CHARGES = 250_000;

    private static final int[] CHRISTMAS_WEAPONS = {33160, 33161, 33162};

    private Player player;

    private int scytheCharges;
    private int whipCharges;
    private int bowCharges;

    public ChristmasWeapons(Player player) {
        this.player = player;
        this.scytheCharges = 0;
        this.whipCharges = 0;
        this.bowCharges = 0;
    }

    public int getCharges(int weaponId) {
        switch (weaponId) {
            case 33160:
                return this.bowCharges;
            case 33161:
                return this.scytheCharges;
            case 33162:
                return this.whipCharges;
        }
        return 0;
    }

    public int getWhipCharges() {return whipCharges;}
    public void setWhipCharges(int charges) {
        this.whipCharges = charges;
    }
    public int getBowChargesCharges() {return bowCharges;}
    public void setBowCharges(int charges) {
        this.bowCharges = charges;
    }
    public int getScytheCharges() {return scytheCharges;}
    public void setScytheCharges(int charges) {
        this.scytheCharges = charges;
    }

    public static void resetCharges(Player player, int weaponId) {
        switch (weaponId) {
            case 33160:
                player.getChristmasWeapons().setBowCharges(0);
                break;
            case 33161:
                player.getChristmasWeapons().setScytheCharges(0);
                break;
            case 33162:
                player.getChristmasWeapons().setWhipCharges(0);
                break;
        }
    }

    public static void manipulateCharges(Player player, int weaponId, int amount) {
        int charges = player.getChristmasWeapons().getCharges(weaponId);
        switch (weaponId) {
            case 33160:
                player.getChristmasWeapons().setBowCharges(charges + amount);
                break;
            case 33161:
                player.getChristmasWeapons().setScytheCharges(charges + amount);
                break;
            case 33162:
                player.getChristmasWeapons().setWhipCharges(charges + amount);
                break;
        }
    }

    public static void removeCharges(Player player, int weaponId, int amount) {
        int charges = player.getChristmasWeapons().getCharges(weaponId);
        if (charges == 0) {
            return;
        }
        switch (weaponId) {
            case 33160:
                player.getChristmasWeapons().setBowCharges(charges - amount);
                break;
            case 33161:
                player.getChristmasWeapons().setScytheCharges(charges - amount);
                break;
            case 33162:
                player.getChristmasWeapons().setWhipCharges(charges - amount);
                break;
        }
    }

    public static void handleItemOption(Player player, int itemId, int option) {
        int charges = player.getChristmasWeapons().getCharges(itemId);
        ItemDef definition = ItemDef.forId(itemId);
        String itemName = definition.getName();

        switch (option) {
            case 2: // Check charges
                checkCharges(player, definition, charges);
                break;
            case 3: // Uncharge
                uncharge(player, definition, charges);
                break;
        }
    }

    private static void checkCharges(Player player, ItemDef itemDef, int charges) {
        String totalCharges = Misc.format(charges);
        player.sendMessage("Your " + itemDef.getName() + " has " + totalCharges + " charges.");
    }

    private static void uncharge(Player player, ItemDef itemDef, int charges) {
        int itemId = itemDef.getId();

        if (charges > 0) {
            ImmutableItem snowballs = new ImmutableItem(10501, charges);
            boolean hasSpace = player.getItems().freeSlots() > 0;

            if (hasSpace) {
                player.getItems().addItem(snowballs.getId(), snowballs.getAmount());
                ChristmasWeapons.resetCharges(player, itemId);
                player.sendMessage("You have removed " + charges + " Snowballs from your " + itemDef.getName() +
                        ".");
            } else
                player.sendMessage("You have no space in your inventory to do that.");
        }
    }

    private static boolean isChristmasWeapon(int... itemsUsed) {
        for (int itemUsed : itemsUsed) {
            if (Arrays.stream(CHRISTMAS_WEAPONS).anyMatch(id -> id == itemUsed)) return true;
        }
        return false;
    }

    public static boolean handleItemOnItem(Player player, int itemUsed, int itemUsedWith) {
        boolean hasSnow = itemUsed == 10501 || itemUsedWith == 10501;
        boolean hasChristmasWeapon = isChristmasWeapon(itemUsed, itemUsedWith);
        // Check if items used are ether and pvp weapon
        if (hasSnow && hasChristmasWeapon) {
            // Determine what item is what
            final int christmasWeapon = isChristmasWeapon(itemUsed) ? itemUsed : itemUsedWith;
            int snow = christmasWeapon == itemUsed ? itemUsedWith : itemUsed;

            ItemDef definition = ItemDef.forId(christmasWeapon);

            int charges = player.getChristmasWeapons().getCharges(christmasWeapon);
            int snowAmount = player.getItems().getItemAmount(snow);

            int chargeSpace = MAX_CHARGES - charges;
            // Check if the player can add more charges
            if (chargeSpace > 0) {
                // Remove ether add charges
                int SnowballsToAdd = Math.min(snowAmount, chargeSpace);
                player.getItems().deleteItem(snow, SnowballsToAdd);
                ChristmasWeapons.manipulateCharges(player, christmasWeapon, SnowballsToAdd);

                player.sendMessage("You have added " + SnowballsToAdd + " Snowballs to your " + definition.getName());

            } else
                player.sendMessage("Your " + definition.getName() + " already has the maximum amount of charges.");

            return true;
        }

        return false;
    }

    public static boolean activateEffect(Player player, int weaponId) {
        return player.getItems().isWearingItem(weaponId, Player.playerWeapon)
                && player.getChristmasWeapons().getCharges(weaponId) > 0;
    }

}
