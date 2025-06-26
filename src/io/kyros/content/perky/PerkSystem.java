package io.kyros.content.perky;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.*;

public class PerkSystem {

    private final Player player;
    public List<GameItem> gameItems = new ArrayList<>();
    public List<Perks> perks = new ArrayList<>();

    public PerkSystem(Player player) {
        this.player = player;
    }

    public void attunePerk(int itemid) {

        SortedMap<String, Perks> map = new TreeMap<String, Perks>();
        for (Perks value : Perks.values()) {
            map.put(value.name(), value);
        }

/*        for (Perks value : map.values()) {
            System.out.println(value);
        }*/

        if (!canAttune(itemid)) {
            return;
        }

        if (!player.getItems().hasItemOnOrInventory(itemid)) {
            player.sendMessage("How the fuck? did you manage to get here?");
            return;
        }

        for (Perks value : Perks.values()) {
            if (value.itemID == itemid && player.getItems().hasItemOnOrInventory(itemid)) {
                player.getPerkSytem().gameItems.add(new GameItem(itemid, 1));
                player.getItems().deleteItem2(itemid, 1);
                player.sendMessage("You attune " + ItemDef.forId(itemid).getName() + ".");
                perks.add(value);
                break;
            }
        }
        updateInterface(false);
    }

    public void removePerk(int itemid) {
            for (GameItem gameItem : player.getPerkSytem().gameItems) {
                if (gameItem.getId() == itemid) {
                    player.getPerkSytem().gameItems.remove(new GameItem(itemid, 1));
                    player.sendMessage("You unattune " + ItemDef.forId(itemid).getName() + ".");
                    player.getItems().addItemUnderAnyCircumstance(itemid, 1);
                    Arrays.stream(Perks.values()).forEach(p -> {
                        if (p.itemID == itemid) {
                            perks.remove(p);
                        }
                    });
                    break;
                }
            }
        updateInterface(true);
    }

    public List<GameItem> gameItems() {
        return gameItems;
    }

    public boolean canAttune(int itemid) {
        if (player.getPerkSytem().gameItems.size() == 9) {
            player.sendMessage("You cannot attune anymore perk's you already have 9 attuned.");
            return false;
        }
        int count = 0;
        for (GameItem gameItem : player.getPerkSytem().gameItems) {
            if (gameItem.getId() == 33112 && itemid == 33112) {
                count++;
            }
            if (count >= 2) {
                player.sendMessage("You can only equip two of these perks!");
                return false;
            }
        }


        if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == itemid && itemid != 33112)) {
            player.sendMessage("You cannot attune anymore of this perk, as you already have one attuned.");
            return false;
        }

/*        int amt = 0;
        for (Perks value : Perks.values()) {
            if (player.getPerkSytem().gameItems.stream().anyMatch(i -> i.getId() == value.itemID) && value.perkType == PerkType.COMBAT && value.itemID == itemid) {
                amt++;
            }
        }

        if (amt >= 2) {
            player.sendMessage("You can only equip 2 combat perk's at one time.");
            return false;
        }*/

        if (player.wildLevel > 0) {
            player.sendMessage("You cannot attune perk's while in the wilderness.");
            return false;
        }

        return true;
    }

    public void updateInterface(boolean remove) {
        if (remove) {
            for (int i = 0; i < 9; i++) {
                player.getPA().itemOnInterface(-1,0, 65022, i);
            }
        }

        if (player.getPerkSytem().gameItems.isEmpty()) {
            return;
        }

        for (int i = 0; i < player.getPerkSytem().gameItems.size(); i++) {
            player.getPA().itemOnInterface(player.getPerkSytem().gameItems.get(i).getId(),player.getPerkSytem().gameItems.get(i).getAmount(), 65022, i);
        }
    }

    public boolean obtainPerk(int itemID) {
        ArrayList<Perks> perks = new ArrayList<>();

        if (!perks.isEmpty()) {
            perks.clear();
        }

        if (itemID == 26547) { // Combat
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.COMBAT) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        } else if (itemID == 26546) { // Skilling
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.SKILLING) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        } else if (itemID == 26548) { // Misc
            if (System.currentTimeMillis() - player.clickDelay <= 2200) {
                return true;
            }
            player.clickDelay = System.currentTimeMillis();
            for (Perks value : Perks.values()) {
                if (value.perkType == PerkType.MISC) {
                    perks.add(value);
                }
            }
            player.getItems().deleteItem2(itemID, 1);
            player.getItems().addItemUnderAnyCircumstance(perks.get(Misc.random((perks.size()-1))).itemID, 1);
            return true;
        }
        return false;
    }
}
