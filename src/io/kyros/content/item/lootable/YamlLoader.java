package io.kyros.content.item.lootable;

import io.kyros.model.items.GameItem;
import lombok.Getter;
import lombok.Setter;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlLoader {
    private static final String CONFIG_DIR = "etc/cfg/mysteryboxes/";

    public static LoadedBox loadItems(String filename) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(Paths.get(CONFIG_DIR, filename + ".yml").toFile())) {
            ItemMapType itemMapType = yaml.loadAs(inputStream, ItemMapType.class);
            Map<LootRarity, List<GameItem>> items = itemMapType.toItemMap();
            Map<LootRarity, Integer> rates = itemMapType.toRateMap();

            // Set default rates if none are provided
            if (rates == null || rates.isEmpty()) {
                rates = getDefaultRates();
            }

            return new LoadedBox(items, rates);
        } catch (Exception e) {
            e.printStackTrace();
            return new LoadedBox(new HashMap<>(), getDefaultRates()); // Ensure defaults are always applied on error
        }
    }

    private static Map<LootRarity, Integer> getDefaultRates() {
        Map<LootRarity, Integer> defaultRates = new HashMap<>();
        defaultRates.put(LootRarity.VERY_RARE, 167);    // 1/60 chance
        defaultRates.put(LootRarity.RARE, 300);        // 1/30 chance
        defaultRates.put(LootRarity.UNCOMMON, 600);   // 1/6 chance
        // The remaining probability implicitly goes to COMMON
        return defaultRates;
    }

    public static class ItemMapType {
        @Setter
        @Getter
        private List<GameItem> common;
        @Setter
        @Getter
        private List<GameItem> rare;
        @Setter
        @Getter
        private List<GameItem> uncommon;
        @Setter
        @Getter
        private List<GameItem> very_rare;
        @Setter
        @Getter
        private Map<String, Integer> rates;

        public Map<LootRarity, List<GameItem>> toItemMap() {
            Map<LootRarity, List<GameItem>> items = new HashMap<>();
            if (common != null && !common.isEmpty()) {
                items.put(LootRarity.COMMON, common);
            }
            if (rare != null && !rare.isEmpty()) {
                items.put(LootRarity.RARE, rare);
            }
            if (uncommon != null && !uncommon.isEmpty()) {
                items.put(LootRarity.UNCOMMON, uncommon);
            }
            if (very_rare != null && !very_rare.isEmpty()) {
                items.put(LootRarity.VERY_RARE, very_rare);
            }
            return items;
        }

        public Map<LootRarity, Integer> toRateMap() {
            if (rates == null || rates.isEmpty()) {
                return null;
            }
            Map<LootRarity, Integer> rateMap = new HashMap<>();
            if (rates.containsKey("common")) {
                rateMap.put(LootRarity.COMMON, rates.get("common"));
            }
            if (rates.containsKey("uncommon")) {
                rateMap.put(LootRarity.UNCOMMON, rates.get("uncommon"));
            }
            if (rates.containsKey("rare")) {
                rateMap.put(LootRarity.RARE, rates.get("rare"));
            }
            if (rates.containsKey("very_rare")) {
                rateMap.put(LootRarity.VERY_RARE, rates.get("very_rare"));
            }
            return rateMap;
        }
    }

    @Getter
    public static class LoadedBox {
        private final Map<LootRarity, List<GameItem>> items;
        private final Map<LootRarity, Integer> rates;

        public LoadedBox(Map<LootRarity, List<GameItem>> items, Map<LootRarity, Integer> rates) {
            this.items = items;
            this.rates = rates;
        }
    }
}
