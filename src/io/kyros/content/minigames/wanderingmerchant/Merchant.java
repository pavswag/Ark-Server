package io.kyros.content.minigames.wanderingmerchant;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.shops.ShopItem;
import io.kyros.model.world.ShopHandler;
import io.kyros.model.entity.player.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Merchant {

    private static final int MERCHANT_ID = 155;
    private static final String MERCHANT_NAME = "Mini Prophet";
    private static final int MERCHANT_RANK_ID = 35;
    private static final long SPAWN_INTERVAL = 60 * 60 * 1000; // 60 minutes in milliseconds
    private static final long DURATION = 60 * 1000; // 45 seconds in milliseconds
    public static boolean spawned = false;
    private static long lastSpawn = 0;
    public static int SHOP_ID;

    private static WanderingLocations currentLocation;

    private static void broadcastLocation() {
        String message = new MessageBuilder()
                .shadow(1)
                .color(MessageColor.ORANGE)
                .text("The ")
                .rank(MERCHANT_RANK_ID) // Display rank before the name
                .color(MessageColor.RED)
                .text(MERCHANT_NAME)
                .color(MessageColor.ORANGE)
                .text(" has spawned ")
                .color(MessageColor.RED)
                .text(currentLocation.getDescription())
                .color(MessageColor.ORANGE)
                .text("! Hurry and check out his rare items!")
                .build();

        // Broadcast the message to all players
        PlayerHandler.executeGlobalMessage(message);
    }

    public static void sendLocalMessage(Player player) {
        if (!spawned) {
            return;
        }
        String message = new MessageBuilder()
                .shadow(1)
                .color(MessageColor.ORANGE)
                .rank(MERCHANT_RANK_ID) // Display rank before the name
                .text("The ")
                .color(MessageColor.RED)
                .text(MERCHANT_NAME)
                .color(MessageColor.ORANGE)
                .text(" has spawned ")
                .color(MessageColor.RED)
                .text(currentLocation.getDescription())
                .color(MessageColor.ORANGE)
                .text("! Hurry and check out his rare items!")
                .build();

        player.sendMessage(message);
    }

    //TODO Change back to GameThread
    public static void handleTick() {
        long currentTime = System.currentTimeMillis();

        // Check if it's time to spawn the merchant
        if (!spawned && currentTime - lastSpawn >= SPAWN_INTERVAL) {
            spawnMerchant();
        }

        // Check if the merchant should be despawned
        if (spawned && currentTime - lastSpawn >= DURATION) {
            despawnMerchant();
        }
    }

    private static void despawnMerchant() {
        spawned = false;
        String message = new MessageBuilder()
                .shadow(1)
                .rank(MERCHANT_RANK_ID) // Display rank before the name
                .color(MessageColor.ORANGE)
                .text(MERCHANT_NAME + " has left! He will return in an hour.")
                .build();

        PlayerHandler.executeGlobalMessage(message);

        for (NPC npc : Server.getNpcs()) {
            if (npc != null) {
                if (npc.getNpcId() == MERCHANT_ID) {
                    npc.unregisterInstant();
                }
            }
        }

        for (Player player : Server.getPlayers()) {
            if (player != null) {
                if (player.myShopId == SHOP_ID) {
                    player.getPA().closeAllWindows();
                }
            }
        }
    }

    private static void spawnMerchant() {
        spawned = true;
        lastSpawn = System.currentTimeMillis(); // Reset the spawn time

        // Randomly select a location for the merchant
        selectRandomLocation();

        // Broadcast the selected location
        broadcastLocation();

        // Add logic here to actually spawn the merchant in the game world.
        createMerchantShop();

        NPCSpawning.spawnNpc(MERCHANT_ID, currentLocation.getPosition().getX(), currentLocation.getPosition().getY(), currentLocation.getPosition().getHeight(), 1, 0);
    }

    private static void selectRandomLocation() {
        WanderingLocations[] locations = WanderingLocations.values();
        Random random = new Random();
        currentLocation = locations[random.nextInt(locations.length)];
    }

    private static void createMerchantShop() {
        List<ShopItem> shopItems = new ArrayList<>();
        for (WanderingItems item : WanderingItems.values()) {
            shopItems.add(new ShopItem(item.getId()+1, 1, item.getCost()));
        }

        // Shuffle the list of shop items to ensure randomness
        Collections.shuffle(shopItems);

        // Add the selected items to the shop
        SHOP_ID = ShopHandler.addShopAnywhere(MERCHANT_NAME, shopItems);
    }

    public static void openMerchantShop(Player player) {
        if (ShopHandler.getShopItems(SHOP_ID).isEmpty()) {
            createMerchantShop();
        }

        player.getShops().openShop(SHOP_ID);
    }
}
