package io.kyros.content.deals;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/03/2024
 */
public class BonusItems {
    private static final Logger logger = LoggerFactory.getLogger(BonusItems.class);

    public static void openInterface(Player player) {
        player.setOpenInterface(24535);

        int frameOffset = 0; // Adjust frame offset as needed

        if (!offers.isEmpty()) {
            for (BonusOffer offer : offers) {
                // Display buy item
                player.getPA().itemOnInterface(offer.buyItem.id, offer.buyItem.amount, 24550 + frameOffset, 0);

                // Display reward item
                player.getPA().itemOnInterface(offer.rewardItem.id, offer.rewardItem.amount, 24551 + frameOffset, 0);

                // Display sale message
                player.getPA().sendString(24558 + (frameOffset / 2), getSaleMessage(offer));

                frameOffset += 2; // Increase frame offset by 2 for the next offer
            }
        }

        player.getPA().showInterface(24535);
    }

    private static String getSaleMessage(BonusOffer offer) {
        String message = "";

        message = "Buy " + ItemDef.forId(offer.buyItem.id).getName() + " x"+ offer.buyItem.amount+ " and recieve "
                + ItemDef.forId(offer.rewardItem.id).getName() + " x" + offer.rewardItem.amount;

        return wrapText(message, 18);
    }

    public static String wrapText(String text, int maxLength) {
        StringBuilder result = new StringBuilder();
        String[] words = text.split("\\s+");
        StringBuilder line = new StringBuilder();
        int lineLength = 0;

        for (String word : words) {
            if (lineLength + word.length() <= maxLength) {
                line.append(word).append(" ");
                lineLength += word.length() + 1;
            } else {
                result.append(line.toString().trim()).append("\\n");
                line = new StringBuilder(word + " ");
                lineLength = word.length() + 1;
            }
        }
        result.append(line.toString().trim()); // Append the last line
        return result.toString();
    }

    public static void handleDonation(Player player, int buyItem, int buyAmount) {
        Optional<BonusOffer> optionalOffer = offers.stream()
                .filter(offer -> offer.buyItem.id == buyItem && buyAmount >= offer.buyItem.amount)
                .findFirst();

        if (optionalOffer.isPresent()) {
            BonusOffer offer = optionalOffer.get();
            int numRewards = buyAmount / offer.buyItem.amount; // Calculate the number of rewards based on the number of items purchased
            player.sendMessage("@red@You have just got "+ numRewards + " bonus item(s) " + ItemDef.forId(offer.rewardItem.id).getName() + " x " + offer.rewardItem.amount * numRewards);
            player.getItems().addItemUnderAnyCircumstance(offer.rewardItem.id, offer.rewardItem.amount * numRewards);
        }
    }

    private static final String FILE_PATH = "/deals/bonus_items.yaml";

    private static List<BonusOffer> offers = new ArrayList<>();
    private static List<BonusOffer> real_offers = new ArrayList<>();
    private static long delay_timer = 0;

    @PostInit
    public static void load() {
        if (!offers.isEmpty()) {
            offers.clear();
            offers = new ArrayList<>();
        }

        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                logger.info("Bonus items YAML file was missing, so a new one was created.");
                return;
            }

            Yaml yaml = new Yaml();
            try (FileReader fileReader = new FileReader(file)) {
                Map<String, Map<String, List<Map<String, Object>>>> yamlData = yaml.load(fileReader);
                if (yamlData != null) {
                    for (Map.Entry<String, Map<String, List<Map<String, Object>>>> entry : yamlData.entrySet()) {
                        String offerKey = entry.getKey();
                        Map<String, List<Map<String, Object>>> offerData = entry.getValue();

                        List<Map<String, Object>> buyItemData = offerData.get("buyItem");
                        List<Map<String, Object>> rewardData = offerData.get("reward");

                        if (buyItemData != null && rewardData != null && !buyItemData.isEmpty() && !rewardData.isEmpty()) {
                            BonusItem buyItem = parseItemData(buyItemData.get(0));
                            BonusItem rewardItem = parseItemData(rewardData.get(0));

                            offers.add(new BonusOffer(offerKey, buyItem, rewardItem));
                        }
                    }
                    logger.info("Loaded " + offers.size() + " bonus items offers from YAML file.");
                } else {
                    logger.info("No bonus items offers found in YAML file.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Error loading bonus items offers: " + e.getMessage());
        }
    }

    private static void save() {
        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            FileWriter fileWriter = new FileWriter(file);

            Yaml yaml = new Yaml();
            Map<String, Map<String, List<Map<String, Object>>>> yamlData = new LinkedHashMap<>();
            for (BonusOffer offer : offers) {
                List<Map<String, Object>> buyItemData = new ArrayList<>();
                buyItemData.add(serializeItem(offer.buyItem));

                List<Map<String, Object>> rewardItemData = new ArrayList<>();
                rewardItemData.add(serializeItem(offer.rewardItem));

                Map<String, List<Map<String, Object>>> offerData = new LinkedHashMap<>();
                offerData.put("buyItem", buyItemData);
                offerData.put("reward", rewardItemData);

                yamlData.put(offer.key, offerData);
            }

            yaml.dump(yamlData, fileWriter);

            fileWriter.close();
            logger.info("Saved " + offers.size() + " bonus items offers to YAML file.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Error saving bonus items offers: " + e.getMessage());
        }
    }

    private static BonusItem parseItemData(Map<String, Object> itemData) {
        int id = (int) itemData.get("id");
        int amount = (int) itemData.get("amount");
        return new BonusItem(id, amount);
    }

    private static Map<String, Object> serializeItem(BonusItem item) {
        return Map.of(
                "id", item.id,
                "amount", item.amount
        );
    }

    public static class BonusItem {
        private int id;
        private int amount;

        public BonusItem(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }
    }

    public static class BonusOffer {
        private String key;
        private BonusItem buyItem;
        private BonusItem rewardItem;

        public BonusOffer(String key, BonusItem buyItem, BonusItem rewardItem) {
            this.key = key;
            this.buyItem = buyItem;
            this.rewardItem = rewardItem;
        }
    }
}
