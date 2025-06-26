package io.kyros.model.entity.player;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.combat.melee.MeleeData;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.ItemStats;
import io.kyros.model.items.ContainerUpdate;
import io.kyros.model.items.CosmeticBoostsHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 15/02/2024
 */
public class CosmeticOverride {

    private static final int INTERFACE_ID = 42669;

    public static void openInterface(Player c) {
        if (c.isBoundaryRestricted()) {
            return;
        }
        updateCosmeticInterface(c);
        c.getPA().showInterface(INTERFACE_ID);
        c.setOpenInterface(INTERFACE_ID);
    }

    private static void updateInventory(Player player) {
        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
        player.getItems().addContainerUpdate(ContainerUpdate.EQUIPMENT);
//        player.getItems().sendWeapon(player.getEquipmentToShow(Player.playerWeapon));
        player.updateItems = true;
        player.getItems().calculateBonuses();
        MeleeData.setWeaponAnimations(player);
        player.setUpdateRequired(true);
        player.setAppearanceUpdateRequired(true);
        player.getItems().sendEquipmentContainer();
        player.getPA().requestUpdates();
        player.getBoostHandler().calculateBoosts(player.playerEquipmentCosmetic);
    }

    public static void handleEquipCosmetic(Player player, ItemDef itemDef, int slot) {
        int targetSlot = ItemStats.forId(itemDef.getId()).getEquipment().getSlot();
        int item = itemDef.getId();

        if (targetSlot == -1) {
            player.sendMessage("This item cannot be worn.");
            return;
        }

        if (player.playerEquipmentCosmetic[targetSlot] > 0) {
            player.getItems().addItemUnderAnyCircumstance(player.playerEquipmentCosmetic[targetSlot], 1);
        }
        player.playerEquipmentCosmetic[targetSlot] = item;
        player.getItems().deleteItem(item, 1);
        updateInventory(player);
        openInterface(player);
    }

    public static boolean handleUnequipCosmetic(Player c, int removeId) {
        int[] equipmentCosmetic = c.playerEquipmentCosmetic;
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        if (interfaceIds.containsValue(removeId)) {

            if (c.isBoundaryRestricted()) {
                return true;
            }
            int slot = getKeyByValue(interfaceIds, removeId);
            c.getItems().addItemUnderAnyCircumstance(equipmentCosmetic[slot], 1);
            equipmentCosmetic[slot] = -1;
            updateInventory(c);
            openInterface(c);
            return true;
        }
        return false;
    }

    public static void setAllOverrides(Player player, boolean bool) {
        for (int i = 0; i < player.cosmeticOverrides.length; i++) {
            player.cosmeticOverrides[i] = bool;
        }
        updateInventory(player);
    }

    public static boolean handleToggleCosmetic(Player c, int toggleId) {
        boolean[] equipmentCosmetic = c.cosmeticOverrides;
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        if (interfaceIds.containsValue(toggleId)) {
            if (c.isBoundaryRestricted()) {
                return true;
            }
            int slot = getKeyByValue(interfaceIds, toggleId);
            equipmentCosmetic[slot] = !equipmentCosmetic[slot];
            updateInventory(c);
            return true;
        }

        return false;
    }
    private static Integer getKeyByValue(Map<Integer, Integer> map, Integer value) {
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return -1; // If the value is not found, return null
    }

    private static void updateCosmeticInterface(Player player) {
        int[] equipmentCosmetic = player.playerEquipmentCosmetic;

        // Map equipment slot index to interface ID
        Map<Integer, Integer> interfaceIds = new HashMap<>();
        interfaceIds.put(0, 42674);
        interfaceIds.put(1, 42676);
        interfaceIds.put(2, 42677);
        interfaceIds.put(3, 42679);
        interfaceIds.put(4, 42680);
        interfaceIds.put(5, 42681);
        interfaceIds.put(7, 42682);
        interfaceIds.put(9, 42683);
        interfaceIds.put(10, 42684);
        interfaceIds.put(12, 42685);
        interfaceIds.put(13, 42678);
        interfaceIds.put(14, 42675);
        interfaceIds.put(15, 42675);

        for (int i = 0; i < equipmentCosmetic.length; i++) {
            if (interfaceIds.containsKey(i)) {
                player.getPA().itemOnInterface(equipmentCosmetic[i], 1, interfaceIds.get(i), 0);
            }
        }
    }
}
