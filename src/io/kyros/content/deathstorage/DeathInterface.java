package io.kyros.content.deathstorage;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;

import java.util.Timer;
import java.util.TimerTask;

public class DeathInterface {

    private Player player;

    public DeathInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonID) {
        switch (buttonID) {
            case 36008:
                if (player.DeathStorageLock) {
                    unlock();
                } else {
                    retrieveItems();
                }
                return true;
        }
        return false;
    }

    public void drawInterface(boolean reset) {
        if (player.getDeathStorage() != null) {
            int amt = (player.getDeathStorage().size() > 0 ? player.getDeathStorage().size()+1 : 0);
            for (int i = 0; i < amt; i++) {
               if (player.getDeathStorage().size() > i && !reset) {
                   player.getPA().itemOnInterface(player.getDeathStorage().get(i), 36150, i);
               } else {
                   player.getPA().itemOnInterface(-1,1,36150, i);
               }
            }
        }

        player.getPA().sendString(36005, "Death's Office Item Retrieval (" + (reset ? 0 : player.getDeathStorage().size()) + "/245)");

        player.getPA().sendString(36006, "Select an item to retrieve the item." + (player.DeathStorageLock ? "@red@(LOCKED)" : "@gre@(UNLOCKED)"));

        player.getPA().sendString(36007, "Current total cost of all stored items is @gre@" + (player.getDeathStorage().size() > 0 ? player.DeathStorageLock ? Misc.formatNumber(player.getDeathInterface().getUnlockCost()) : Misc.formatNumber(0) : Misc.formatNumber(0)));

        player.getPA().showInterface(36000);
    }

    public void unlock() {
        if (player.getItems().getInventoryCount(995) >= player.getDeathInterface().getUnlockCost()) {
            player.getItems().deleteItem2(995, player.getDeathInterface().getUnlockCost());
            player.DeathStorageLock = false;
            player.sendMessage("[DEATH] You have paid off your balance now and can collect your items!");
        } else {
            player.sendMessage("[DEATH] You don't have enough funds to do that right now.");
        }
        drawInterface(false);
    }

    private  boolean active = false;

    public void retrieveItems() {
        if (active) {
            return;
        }
        active = true;
        TimerTask task = new TimerTask() {
            int tick = 0;
            @Override
            public void run() {
                if (tick == 1) {
                    for (GameItem gameItem : player.getDeathStorage()) {
                        if (player.getItems().hasRoomInInventory(gameItem.getId(), gameItem.getAmount())) {
                            player.getInventory().addOrDrop(new ImmutableItem(gameItem.getId(), gameItem.getAmount()));
                        } else {
                            player.getInventory().addToBank(new ImmutableItem(gameItem.getId(), gameItem.getAmount()));
                        }
                    }
                    player.sendMessage("[DEATH] All item's have been split between your bank & your inventory!");
                    drawInterface(true);
                } else if (tick == 2) {
                    active = false;
                    cancel();
                    player.getDeathStorage().clear();
                }
                tick++;
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 100, 100);
    }

    public void retrieveSlotItem(int slot) {
        if (player.getDeathStorage().get(slot) != null) {
            if (player.DeathStorageLock && player.getDeathStorage().get(slot).getId() != 995) {
                player.sendMessage("[DEATH] You haven't paid for the usage of Death's Item Retrieval!");
                return;
            }
            GameItem gameItem = player.getDeathStorage().get(slot);
            if (player.getItems().hasRoomInInventory(gameItem.getId(), gameItem.getAmount())) {
                player.getInventory().addOrDrop(new ImmutableItem(gameItem.getId(), gameItem.getAmount()));
            } else {
                player.getInventory().addToBank(new ImmutableItem(gameItem.getId(), gameItem.getAmount()));
                player.sendMessage("[DEATH] Your inventory was full so the item was sent to your bank!");
            }
            player.getDeathStorage().remove(gameItem);
            drawInterface(false);
        }
    }

    public int getUnlockCost() {
        if (player.getPA().calculateTotalLevel() < 500)
            return 0;

        int v = (int) (200000 + (((player.getPA().calculateTotalLevel() - 500) / (2277d - 500)) * 800000));

        /* Donator perk for free unlocks */
        if (player.amDonated >= 50) {
            v = 0;
        }

        return v;

    }
}
