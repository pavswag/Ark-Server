package io.kyros.content.minigames.donationgames;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
public enum TreasureGames {
    DONATION_PARADISE,
    KINGS_BEDROOM,
    MADNESS_MAZE,
    TRAIL_OF_TREASURE,
    TREASURE_BUSTER,
    TREASURE_HUNTER,
    BANK_VAULT;

    private String miniGameName;
    private Boundary boundary;
    private Position startPosition;
    private List<GlobalObject> objectList;
    private List<GameItem> items;
    private Map<GameItem, LootRarity> itemRarityMap = new HashMap<>();
    private GameItem specialItem; // New field for special item

    private static final Logger logger = LoggerFactory.getLogger(TreasureGames.class);

    public void loadFromYaml(InputStream inputStream) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);

        try {
            this.miniGameName = (String) data.get("miniGameName");

            Map<String, Integer> boundaryMap = (Map<String, Integer>) data.get("boundary");
            if (boundaryMap != null) {
                this.boundary = new Boundary(
                        boundaryMap.get("minX"),
                        boundaryMap.get("minY"),
                        boundaryMap.get("highX"),
                        boundaryMap.get("highY"),
                        boundaryMap.getOrDefault("height", 0)  // Default to 0 if height is not specified
                );
            } else {
                logger.error("Boundary data is missing for mini game: {}", miniGameName);
            }

            Map<String, Integer> startPositionMap = (Map<String, Integer>) data.get("startPosition");
            if (startPositionMap != null) {
                this.startPosition = new Position(
                        startPositionMap.get("x"),
                        startPositionMap.get("y"),
                        startPositionMap.getOrDefault("z", 0)  // Default to 0 if height is not specified
                );
            } else {
                logger.error("Start position data is missing for mini game: {}", miniGameName);
            }

            this.objectList = new ArrayList<>();
            List<Map<String, Object>> objectListData = (List<Map<String, Object>>) data.get("objectList");
            if (objectListData != null) {
                for (Map<String, Object> obj : objectListData) {
                    try {
                        int id = (int) obj.get("id");
                        Map<String, Integer> posMap = (Map<String, Integer>) obj.get("position");
                        Position position = new Position(posMap.get("x"), posMap.get("y"), 0);
                        this.objectList.add(new GlobalObject(id, position, 0, 10));
                    } catch (ClassCastException | NullPointerException e) {
                        logger.error("Error loading object list for mini game: {}", miniGameName, e);
                    }
                }
            } else {
                logger.error("Object list data is missing for mini game: {}", miniGameName);
            }

            this.items = new ArrayList<>();
            List<Map<String, Object>> itemsData = (List<Map<String, Object>>) data.get("items");
            if (itemsData != null) {
                for (Map<String, Object> itemMap : itemsData) {
                    try {
                        int id = (int) itemMap.get("id");
                        int amount = (int) itemMap.get("amount");
                        String rarityString = (String) itemMap.get("rarity");
                        LootRarity rarity = LootRarity.valueOf(rarityString);
                        GameItem item = new GameItem(id, amount);
                        this.items.add(item);
                        this.itemRarityMap.put(item, rarity);
                    } catch (ClassCastException | NullPointerException | IllegalArgumentException e) {
                        logger.error("Error loading items for mini game: {}", miniGameName, e);
                    }
                }
            } else {
                logger.error("Items data is missing for mini game: {}", miniGameName);
            }

            List<Map<String, Integer>> specialItemsData = (List<Map<String, Integer>>) data.get("specialitem");
            if (specialItemsData != null && !specialItemsData.isEmpty()) {
                Map<String, Integer> specialItemMap = specialItemsData.get(0);
                this.specialItem = new GameItem(specialItemMap.get("id"), specialItemMap.getOrDefault("amount", 1));
            } else {
                logger.error("Special item data is missing for mini game: {}", miniGameName);
            }
        } catch (Exception e) {
            logger.error("Error loading mini game configuration: {}", miniGameName, e);
        }
    }

    public static void loadAllFromDirectory(String directoryPath) {
        try (Stream<Path> paths = Files.list(Paths.get(directoryPath))) {
            paths.filter(path -> path.toString().endsWith(".yaml"))
                    .forEach(path -> {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            Yaml yaml = new Yaml();
                            Map<String, Object> data = yaml.load(inputStream);
                            String miniGameName = (String) data.get("miniGameName");

                            for (TreasureGames game : TreasureGames.values()) {
                                if (game.name().equalsIgnoreCase(miniGameName.replace(" ", "_"))) {
                                    game.loadFromYaml(Files.newInputStream(path));
                                    break;
                                }
                            }
                        } catch (IOException e) {
                            logger.error("Error loading YAML from path: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            logger.error("Error listing YAML files in directory: {}", directoryPath, e);
        }
    }

    @Getter
    @Setter
    public static class TreasureGameConfig {
        private String miniGameName;
        private Boundary boundary;
        private Position startPosition;
        private List<Map<String, Object>> objectList;
        private List<Map<String, Object>> items;
        private List<Map<String, Integer>> specialitem; // New field for special item
    }

    @PostInit
    public static void loadAllMinigames() {
        String directoryPath = Server.getDataDirectory() + "/cfg/minigame";
        loadAllFromDirectory(directoryPath);

/*        for (TreasureGames game : TreasureGames.values()) {
            System.out.println("Game Name: " + game.getMiniGameName());
            System.out.println("Boundary: " + game.getBoundary().toString());
            System.out.println("Start Position: " + game.getStartPosition());
            System.out.println("Object List: " + game.getObjectList());
            System.out.println("Items: " + game.getItems());
            System.out.println("Special Item: " + (game.getSpecialItem() != null ? game.getSpecialItem().getId() : "None"));
            System.out.println();
        }*/
    }
}
