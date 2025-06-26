package io.kyros.content.minigames.castlewars;

import io.kyros.model.entity.player.Player;
import io.kyros.model.definitions.ItemStats;

public enum CastleWarsEquipment {
    SARA_MAGE(
            new int[]{20586, 4675, 4091, 4093, 3105, 7462, 3842, 2550},
            new int[]{562, 560, 555}
    ),
    ZAMMY_MAGE(
            new int[]{20586, 4675, 4101, 4103, 3105, 7462, 3842, 2550},
            new int[]{562, 560, 555}
    ),
    RANGE(
            new int[]{20586, 9185, 20423, 20424, 3105, 7462, 3842, 2550},
            new int[]{9144}
    ),
    MELEE(
            new int[]{20586, 4587, 1127, 1079, 3105, 7462, 3842, 2550},
            new int[]{5698}
    );

    private final int[] equipment;
    private final int[] inventory;

    CastleWarsEquipment(int[] equipment, int[] inventory) {
        this.equipment = equipment;
        this.inventory = inventory;
    }

    public int[] getEquipment() {
        return equipment;
    }

    public int[] getInventory() {
        return inventory;
    }

    public void equipPlayer(Player player) {
        // Equip the items
        for (int itemId : equipment) {
            player.getItems().equipItem(itemId, 1, ItemStats.forId(itemId).getEquipment().getSlot());
        }
        // Add inventory items
        for (int itemId : inventory) {
            player.getItems().addItemUnderAnyCircumstance(itemId, (itemId == 562 || itemId == 560 || itemId == 555 || itemId == 9144) ? 5000 : 1);
        }
    }

    //Melee Sprite index = 5
    //Range Sprite index = 7
    //Mage Sprite index = 9

    public static void displayInterface(Player player) {
        if (player.CastleWarsEquip < 1) {
            player.CastleWarsEquip = 1;
        }
        reloadInterface(player);
        player.getPA().showInterface(27550);
    }

    private static void reloadInterface(Player player) {
        // Implement the method based on your game's needs
        player.getPA().sendString(27562, "Currently Selected: " + (player.CastleWarsEquip == 1 ? "<icon=134> Melee <icon=134>" : player.CastleWarsEquip == 2 ? "<icon=137> Range <icon=137>" : "<icon=139> Mage <icon=139>") + " ");
        //Send sprite change for Sword, & Description box
        if (player.CastleWarsEquip == 1) {
            player.getPA().sendChangeSprite(27563, (byte) 5);
            player.getPA().sendChangeSprite(27554, (byte) 3);
            player.getPA().sendChangeSprite(27557, (byte) 2);
            player.getPA().sendChangeSprite(27560, (byte) 2);
        } else if (player.CastleWarsEquip == 2) {
            player.getPA().sendChangeSprite(27563, (byte) 7);
            player.getPA().sendChangeSprite(27554, (byte) 2);
            player.getPA().sendChangeSprite(27557, (byte) 3);
            player.getPA().sendChangeSprite(27560, (byte) 2);
        } else {
            player.getPA().sendChangeSprite(27563, (byte) 9);
            player.getPA().sendChangeSprite(27554, (byte) 2);
            player.getPA().sendChangeSprite(27557, (byte) 2);
            player.getPA().sendChangeSprite(27560, (byte) 3);
        }

    }

    public static boolean handleInterfaceButton(Player player, int buttonId) {
        if (buttonId >= 27553 && buttonId <= 27559) {
            int equipmentType = getEquipmentTypeFromButton(buttonId);
            player.CastleWarsEquip = equipmentType;
            displayInterface(player);
            return true;
        } else if (buttonId == 27563) {
            player.getItems().clearEquipment();
            player.getPA().removeAllItems();

            equipTeamSpecificItems(player);
            getEquipmentForCurrentSelection(player).equipPlayer(player);

            player.getPA().closeAllWindows();
            return true;
        }
        return false;
    }

    public static void forceEquipOnJoin(Player player) {
        player.getItems().clearEquipment();
        player.getPA().removeAllItems();

        equipTeamSpecificItems(player);
        getEquipmentForCurrentSelection(player).equipPlayer(player);
    }

    private static int getEquipmentTypeFromButton(int buttonId) {
        switch (buttonId) {
            case 27553: return 1; // Melee
            case 27556: return 2; // Range
            case 27559: return 3; // Mage
            default: return 0;
        }
    }

    private static CastleWarsEquipment getEquipmentForCurrentSelection(Player player) {
        switch (player.CastleWarsEquip) {
            case 1: return MELEE;
            case 2: return RANGE;
            case 3:
                player.setSidebarInterface(6, 838);
                player.playerMagicBook = 1;
                player.getPA().resetAutocast();
                return CastleWarsLobby.getTeamNumber(player) == 1 ? SARA_MAGE : ZAMMY_MAGE;
            default:
                player.CastleWarsEquip = 1;
                return MELEE;
        }
    }

    private static void equipTeamSpecificItems(Player player) {
        int teamNumber = CastleWarsLobby.getTeamNumber(player);
        if (teamNumber == 1) {
            player.getItems().equipItem(4514, 1, Player.playerCape);
            player.getItems().equipItem(4513, 1, Player.playerHat);
        } else {
            player.getItems().equipItem(4516, 1, Player.playerCape);
            player.getItems().equipItem(4515, 1, Player.playerHat);
        }
    }
}
