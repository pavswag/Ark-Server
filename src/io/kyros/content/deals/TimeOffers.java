package io.kyros.content.deals;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.SlottedItem;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class TimeOffers {
    private static final String FILE_PATH = "/deals/timed_offers.yaml";
    private static final Logger logger = LoggerFactory.getLogger(TimeOffers.class);

    private static ArrayList<Offer> offers = new ArrayList<>();

    public static void checkPurchase(Player player, int itemID, int itemAmount) {
        System.out.println(player.getDisplayName() + " / " + itemID + " / " + itemAmount);
        List<Offer> qualifyingOffers = new ArrayList<>();

        // Find all offers that match the itemID and are not expired
        for (Offer offer : offers) {
            if (offer.itemIdToBuy == itemID && !offer.isExpired(offer.totalTime)) {
                qualifyingOffers.add(offer);
            }
        }

        // Process all qualifying offers
        for (Offer offer : qualifyingOffers) {
            if (offer.itemAmountToBuy > itemAmount) {
                System.out.println("Buy amount is greater than item amount ?" + offer.itemAmountToBuy + " / " + itemAmount);
                continue; // Skip to the next offer if itemAmount is not sufficient
            }

            int totalPurchased = itemAmount / offer.itemAmountToBuy; // Calculate total purchases
            if (player.debugMessage) {
                player.sendErrorMessage(totalPurchased + " Amount to get.");
            }

            // Apply rewards based on total purchases
            for (int i = 0; i < totalPurchased; i++) {
                for (Reward reward : offer.rewards) {
                    player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
                    player.sendMessage("@red@You claimed " + reward.getAmount() + " " + ItemDef.forId(reward.getId()).getName() + " from timed offers!");
                }
            }
        }
    }

    public static void tick() {
        checkForNewOffers();
    }

    public static void drawInterface(Player player) {
        update(player);
        player.setOpenInterface(24605);
        player.getPA().showInterface(24605);
    }

    public static void update(Player player) {
        for (int i = 0; i < offers.size(); i++) {
            Offer offer = offers.get(i);
            for (int i1 = 0; i1 < offer.rewards.size(); i1++) {
                player.getPA().itemOnInterface(offer.rewards.get(i1).getId(), offer.rewards.get(i1).getAmount(), 24620 + i, i1);
            }

            long timeRemaining = offer.totalTime - System.currentTimeMillis();

            String timer = (offer.isExpired(offer.totalTime)) ? "@red@EXPIRED" : millisecondsToString(timeRemaining);
            player.getPA().sendString(timer, 24623 + i);
            String desc = wrapText(offer.getDescription(), 19);
            player.getPA().sendString(desc, 24629 + i);
        }
    }

    public static String millisecondsToString(long milliseconds) {
        // Convert milliseconds to seconds
        long seconds = milliseconds / 1000;

        // Calculate hours, minutes, and remaining seconds
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;

        // Format the result as a string
        String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

        return timeString;
    }

    private static void checkForNewOffers() {
        if (offers.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        for (Offer offer : offers) {
            if (offer.isExpired(currentTime)) {
                save();
            }
        }
    }

    public static void save() {
        if (offers.isEmpty()) {
            return;
        }

        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            FileWriter fileWriter = new FileWriter(file); // Overwrite mode, not append

            int offerIndex = 1;
            for (Offer offer : offers) {
                fileWriter.write("offer" + offerIndex + ":\n");
                fileWriter.write("  description: " + offer.description + "\n");
                fileWriter.write("  totalTime: " + offer.totalTime + "\n");
                fileWriter.write("  itemIdToBuy: " + offer.itemIdToBuy + "\n");
                fileWriter.write("  itemAmountToBuy: " + offer.itemAmountToBuy + "\n");
                fileWriter.write("  rewards:\n");
                for (Reward item : offer.rewards) {
                    fileWriter.write("    - id: " + item.getId() + "\n");
                    fileWriter.write("      amount: " + item.getAmount() + "\n");
                }
                fileWriter.write("\n");
                offerIndex++;
            }

            fileWriter.close();
        } catch (IOException e) {
            logger.error("Error saving offers", e);
        }
    }
    public static void forceReloadOffers(Player player) {
        offers.clear();
        load();
        player.sendMessage("All offers were cleared from the system and reloaded from the files!");
        for (Player player1 : Server.getPlayers()) {
            if (player1.isInterfaceOpen(24605)) {
                drawInterface(player1);
                player1.sendMessage("@red@The Offers page was updated while you were viewing it.");
            }
        }
    }

    @PostInit
    public static void load() {
        try {
            createDirectoryIfNotExists();
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                logger.info("TimedOffers.yaml was missing so one was created for you!");
            } else {
                Yaml yaml = new Yaml();
                try (FileReader fileReader = new FileReader(file)) {
                    Map<String, Map<String, Object>> yamlData = yaml.load(fileReader);
                    if (yamlData != null) {
                        for (Map.Entry<String, Map<String, Object>> entry : yamlData.entrySet()) {
                            String offerKey = entry.getKey();
                            Map<String, Object> offerData = entry.getValue();
                            String description = (String) offerData.get("description");
                            long totalTime = (long) offerData.get("totalTime");
                            int itemIdToBuy = (int) offerData.getOrDefault("itemIdToBuy", 0); // Load itemIdToBuy with default value 0 if missing
                            int itemAmountToBuy = (int) offerData.getOrDefault("itemAmountToBuy", 0); // Load itemAmountToBuy with default value 0 if missing
                            List<Reward> rewards = new ArrayList<>();
                            List<Map<String, Object>> rewardsData = (List<Map<String, Object>>) offerData.get("rewards");
                            if (rewardsData != null) {
                                for (Map<String, Object> rewardData : rewardsData) {
                                    int id = (int) rewardData.getOrDefault("id", 0); // Load id with default value 0 if missing
                                    int amount = (int) rewardData.getOrDefault("amount", 0); // Load amount with default value 0 if missing
                                    rewards.add(new Reward(id, amount));
                                }
                            }
                            offers.add(new Offer(description, totalTime, rewards, itemIdToBuy, itemAmountToBuy));
                        }
                        logger.info("[TIMED OFFERS] a total of " + offers.size() + " Timed offers have been loaded into the system!");
                    } else {
                        logger.info("No data found in the TimedOffers.yaml file.");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error loading offers", e);
        }
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
//                System.out.println("Debug: Line length exceeded. Current line: " + line.toString().trim() + ", Length: " + lineLength);
                result.append(line.toString().trim()).append("\\n");
                line = new StringBuilder(word + " ");
                lineLength = word.length() + 1;
            }
        }

//        System.out.println("Debug: Final line: " + line.toString().trim() + ", Length: " + lineLength);
        result.append(line.toString().trim()); // Append the last line
        return result.toString();
    }

    public static class Offer {
        private String description;
        public long totalTime;
        public List<Reward> rewards;
        private int itemIdToBuy;
        private int itemAmountToBuy;

        public Offer(String description, long totalTime, List<Reward> rewards, int itemIdToBuy, int itemAmountToBuy) {
            this.description = description;
            this.totalTime = totalTime;
            this.rewards = rewards;
            this.itemIdToBuy = itemIdToBuy;
            this.itemAmountToBuy = itemAmountToBuy;
        }

        public boolean isExpired(long currentTime) {
            return System.currentTimeMillis() > currentTime;
        }


        public String getDescription() {
            return description;
        }
    }

    public static class Reward {
        private int id;
        private int amount;

        public Reward(int id, int amount) {
            this.id = id;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public int getAmount() {
            return amount;
        }

        // Constructor, getters, and setters
    }

    private static void createDirectoryIfNotExists() {
        File directory = new File(Server.getDataDirectory() + "/deals/");
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (!success) {
                logger.error("Failed to create directory: {}", directory.getAbsolutePath());
            }
        }
    }


    private static void addNewOffer(Player player, int amountToPurchase, int purchaseItemID, int timeToUseInMinutes, int offerIndex) {
        List<Reward> rewards = getRewards(player);
        if (rewards == null || rewards.isEmpty()) {
            player.sendMessage("@red@You need the items and their amounts in your inventory!");
            return;
        }

        // Convert minutes to milliseconds
        long timeToUseInMillis = TimeUnit.MINUTES.toMillis(timeToUseInMinutes);

        // Check if the offer index is valid
        if (offerIndex >= 0 && offerIndex < offers.size()) {
            Offer offerToUpdate = offers.get(offerIndex);
            offerToUpdate.itemAmountToBuy = amountToPurchase;
            offerToUpdate.itemIdToBuy = purchaseItemID;
            if (purchaseItemID == 99999) {
                offerToUpdate.description = "Spend $"+amountToPurchase+" to receive these rewards!";
            } else {
                offerToUpdate.description = "Purchase " + amountToPurchase + "x "
                        + ItemDef.forId(purchaseItemID).getName() + " to get the rewards!";
            }
            offerToUpdate.totalTime = System.currentTimeMillis() + timeToUseInMillis; // Set total time in milliseconds
            offerToUpdate.rewards = rewards;
            save(); // Save the updated offers to the file
            player.sendMessage("@red@You have added/updated an offer! Offers available: " + offers.size());
            drawInterface(player);
        } else {
            player.sendMessage("@red@Invalid offer index!");
        }
    }

    private static List<Reward> getRewards(Player player) {
        if (player.getItems().freeSlots() >= 28) {
            return null;
        }
        List<Reward> new_items = new ArrayList<>();

        for (SlottedItem inventoryItem : player.getItems().getInventoryItems()) {
            new_items.add(new Reward(inventoryItem.getId(), inventoryItem.getAmount()));
        }

        return new_items;
    }

    public static boolean buttonHandler(Player player, int buttonId) {
        if (buttonId == 24626) { // First box
            if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                handleOfferBox(player, 0);
            } else {
                player.sendMessage("@red@All offers are processed automatically when you claim your donation!");
            }
            return true;
        }
        if (buttonId == 24627) { // Second box
            if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                handleOfferBox(player, 1);
            } else {
                player.sendMessage("@red@All offers are processed automatically when you claim your donation!");
            }
            return true;
        }
        if (buttonId == 24628) { // Third box
            if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                handleOfferBox(player, 2);
            } else {
                player.sendMessage("@red@All offers are processed automatically when you claim your donation!");
            }
            return true;
        }
        return false;
    }

    private static void handleOfferBox(Player player, int offerIndex) {
        player.start(new DialogueBuilder(player)
                .option("Would you like to update the offer?",
                        new DialogueOption("Yes", p -> {
                            p.getPA().closeAllWindows();
                            p.getPA().sendEnterString("OfferBox " + (offerIndex + 1) + " (amountToPurchase-purchaseItemID-Mins)",
                                    (plr, str) -> {
                                        String[] args = str.split("-");
                                        if (args.length != 3) {
                                            throw new IllegalArgumentException();
                                        }
                                        int itemAmount = Integer.parseInt(args[0]);
                                        int itemId = Integer.parseInt(args[1]);
                                        int tim = Integer.parseInt(args[2]);
                                        addNewOffer(p, itemAmount, itemId, tim, offerIndex);
                                        player.sendMessage("@red@You have added/updated an offer! Offers available: " + offers.size());
                                        player.getPA().closeAllWindows();
                                    });
                        }),
                        new DialogueOption("No.", p -> p.getPA().closeAllWindows())));
    }
}
