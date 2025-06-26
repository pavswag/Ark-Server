package io.kyros.content.minigames.donationgames;

import io.kyros.Configuration;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class TreasureHandler {

    private static final int SPECIAL_ITEM_CHANCE = 30;
    private static final int VERY_RARE_CHANCE = 99;
    private static final int RARE_CHANCE = 97;
    private static final int UNCOMMON_CHANCE = 35;

    private static final Logger LOGGER = Logger.getLogger(TreasureHandler.class.getName());

    public static TreasureGames getTreasureGame(String gameName) {
        for (TreasureGames game : TreasureGames.values()) {
            if (game.getMiniGameName().equalsIgnoreCase(gameName)) {
                return game;
            }
        }
        return null;
    }

    public static List<GlobalObject> getObjectsForGame(String gameName) {
        TreasureGames game = getTreasureGame(gameName);
        return game != null ? game.getObjectList() : null;
    }

    public static List<GameItem> getItemsForGame(String gameName) {
        TreasureGames game = getTreasureGame(gameName);
        return game != null ? game.getItems() : null;
    }

    public static boolean isGameObject(Player player, GlobalObject go) {
        if (player.treasureGames != null) {
            TreasureGames game = player.treasureGames;
            /*if (!Boundary.isIn(player, game.getBoundary())) {
                return false;
            }*/
            for (GlobalObject globalObject : game.getObjectList()) {
                if (isSameObject(globalObject, go)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isSameObject(GlobalObject obj1, GlobalObject obj2) {
        return obj1.getObjectId() == obj2.getObjectId() &&
                obj1.getPosition().getX() == obj2.getPosition().getX() &&
                obj1.getPosition().getY() == obj2.getPosition().getY();
    }

    public static void handleRandomItem(Player player) {
        if (player.treasureGames != null) {
            TreasureGames game = player.treasureGames;
            Random random = new Random();
            int chance = Misc.random(0, 100);

            if (chance < SPECIAL_ITEM_CHANCE && game.getSpecialItem() != null) {
                awardSpecialItem(player, game);
            } else {
                awardRandomItem(player, game, random);
            }
        }
    }

    private static void awardSpecialItem(Player player, TreasureGames game) {
        GameItem specialItem = game.getSpecialItem();
        player.getItems().addItemUnderAnyCircumstance(specialItem.getId(), specialItem.getAmount());
        announceSpecialLoot(player, specialItem, game.getMiniGameName());
        endGame(player);
    }

    private static void awardRandomItem(Player player, TreasureGames game, Random random) {
        int chance = random.nextInt(100) + 1;
        LootRarity selectedRarity = selectLootRarity(chance);
        List<GameItem> itemsByRarity = getItemsByRarity(selectedRarity, game);

        if (!itemsByRarity.isEmpty()) {
            GameItem selectedItem = itemsByRarity.get(random.nextInt(itemsByRarity.size()));
            player.getItems().addItemUnderAnyCircumstance(selectedItem.getId(), selectedItem.getAmount());
            if (selectedRarity == LootRarity.RARE || selectedRarity == LootRarity.VERY_RARE) {
                announceRareLoot(player, selectedItem, game.getMiniGameName());
            }
            incrementTreasureCollected(player, 1);
        }
    }

    private static LootRarity selectLootRarity(int chance) {
        if (chance >= VERY_RARE_CHANCE) {
            return LootRarity.VERY_RARE;
        } else if (chance >= RARE_CHANCE) {
            return LootRarity.RARE;
        } else if (chance >= UNCOMMON_CHANCE) {
            return LootRarity.UNCOMMON;
        } else {
            return LootRarity.COMMON;
        }
    }

    public static List<GameItem> getItemsByRarity(LootRarity rarity, TreasureGames game) {
        List<GameItem> itemsByRarity = new ArrayList<>();
        if (game != null) {
            for (GameItem item : game.getItems()) {
                if (game.getItemRarityMap().get(item) == rarity) {
                    itemsByRarity.add(item);
                }
            }
        }
        return itemsByRarity;
    }

    public static void incrementTreasureCollected(Player player, int amount) {
        player.treasureCollected += amount;
        updateInterface(player);
        checkGameEnd(player);
    }

    private static void checkGameEnd(Player player) {
        TreasureGames game = player.treasureGames;
        if (game != null && player.treasureCollected >= game.getObjectList().size()) {
            endGame(player);
        }
    }

    public static void addObjectToPlayer(Player player, GlobalObject go) {
        player.treasureObjects.add(go);
    }

    public static boolean objectActivated(Player player, GlobalObject go) {
        for (GlobalObject treasureObject : player.treasureObjects) {
            if (isSameObject(treasureObject, go)) {
                player.sendErrorMessage("You've already got the loot from here!");
                return true;
            }
        }
        return false;
    }

    public static void announceRareLoot(Player player, GameItem item, String minigame) {
        String message = buildRareLootMessage(player, item, minigame);
        PlayerHandler.executeGlobalMessage(message);
    }

    private static String buildRareLootMessage(Player player, GameItem item, String minigame) {
        String displayName = player.getDisplayName();
        String itemName = item.getDef().getName();
        int itemAmount = item.getAmount();

        return new MessageBuilder()
                .shadow(0)
                .bracketed("TREASURE", MessageColor.RED)
                .text(" ")
                .color(MessageColor.BLUE)
                .text(displayName)
                .color(MessageColor.GREEN)
                .text(" has just nabbed ")
                .color(MessageColor.PURPLE)
                .text(itemName + " x " + itemAmount)
                .color(MessageColor.GREEN)
                .text(" from ")
                .color(MessageColor.RED)
                .text(minigame)
                .color(MessageColor.GREEN)
                .text(" minigame (available on Discord store deals)!")
                .build();
    }

    private static void announceSpecialLoot(Player player, GameItem item, String miniGameName) {
        String message = buildSpecialLootMessage(player, item, miniGameName);
        PlayerHandler.executeGlobalMessage(message);
    }

    private static String buildSpecialLootMessage(Player player, GameItem item, String miniGameName) {
        String displayName = player.getDisplayName();
        String itemName = item.getDef().getName();
        int itemsFound = player.treasureCollected;

        return new MessageBuilder()
                .shadow(0)
                .bracketed("TREASURE", MessageColor.RED)
                .text(" ")
                .color(MessageColor.BLUE)
                .text(displayName)
                .color(MessageColor.GREEN)
                .text(" has found a " + itemName + " after finding ")
                .color(MessageColor.PURPLE)
                .text(itemsFound + " items")
                .color(MessageColor.GREEN)
                .text(" in the ")
                .color(MessageColor.RED)
                .text(miniGameName)
                .color(MessageColor.GREEN)
                .text(" minigame (available on Discord store deals)!")
                .build();
    }

    public static void resetGame(Player player) {
        player.treasureObjects.clear();
        player.treasureCollected = 0;
        player.treasureTimer = -1;
        updateInterface(player);
    }

    public static void endGame(Player player) {
        if (player.getInstance()!= null) {
            player.getInstance().remove(player);
        }
        player.getPA().startTeleport(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0, "foundry", false);
        TreasureGames game = player.treasureGames;
        player.sendErrorMessage("[@red@TREASURE@bla@] Your " + game.getMiniGameName() + " game has ended, enjoy your spoils!");
        Achievements.increase(player, AchievementType.TREASURE_GAMES, 1);
        resetGame(player);
        player.treasureGames = null;
    }

    public static void updateInterface(Player player) {
        player.getPA().sendString(24970, String.valueOf(player.treasureCollected)); // Counter

        if (player.treasureTimer > -1) {
            player.getPA().sendString(24973, Misc.formatTime(player.treasureTimer));
        } else {
            player.getPA().sendString(24973, "N/A");
        }
    }
}
