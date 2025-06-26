package io.kyros.content.deals;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.donor.CosmeticManager;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CosmeticDeals {
    private static final String FILE_PATH = "/deals/cosmetic_offers.yaml";
    private static final Logger logger = LoggerFactory.getLogger(CosmeticDeals.class);

    private static List<CosmeticOffer> offers = new ArrayList<>();

    public static void updateOffers(Player player) {
        player.getPA().sendString(24581, "Credits: "+player.getCosmeticCredits());
        if (!offers.isEmpty()) {
            for (int i = 0; i < offers.size(); i++) {
                player.getPA().itemOnInterface(offers.get(i).rewards.itemId, offers.get(i).rewards.itemAmount, 24582+i,0);
                player.getPA().sendString(24588+i, offers.get(i).cost + " Credits");
            }
        }
    }

    public static void openInterface(Player player) {
        player.setOpenInterface(24565);

        updateOffers(player);

        player.getPA().showInterface(24565);
    }

    public static boolean checkCosmeticPurchase(Player player, int buttonId) {
        Optional<CosmeticOffer> optionalOffer = offers.stream()
                .filter(offer -> offer.buttonId == buttonId)
                .findFirst();

        if (player.getRights().isOrInherits(Right.STAFF_MANAGER) && optionalOffer.isPresent()) {
            CosmeticOffer offer = optionalOffer.get();
            int index = offers.indexOf(offer);
            player.start(new DialogueBuilder(player).option("Would you like to edit the offer?", new DialogueOption("Yes", p -> {
                        p.getPA().closeAllWindows();
                        p.getPA().sendEnterString("CosmeticBox " + index + " (cosmeticID-cosmeticAmount-cosmeticCost)",
                                (plr, str) -> {
                                    String[] args = str.split("-");
                                    if (args.length != 3) {
                                        throw new IllegalArgumentException();
                                    }
                                    int itemAmount = Integer.parseInt(args[0]);
                                    int itemId = Integer.parseInt(args[1]);
                                    int cost = Integer.parseInt(args[2]);
                                    addnewCosmeticOffer(p, itemAmount, itemId, cost, index);
                                    player.sendMessage("@red@You have added/updated an offer! Offers available: " + offers.size());
                                    player.getPA().closeAllWindows();
                                });

                    }),
                    new DialogueOption("no (buy item)", p -> {
                        int cost = offer.cost;
                        if (player.getCosmeticCredits() >= cost) {
                            player.setCosmeticCredits(player.getCosmeticCredits() - cost);
                            player.getItems().addItem(offer.rewards.itemId, offer.rewards.itemAmount);
                            player.sendMessage("@red@You claimed " + ItemDef.forId(offer.rewards.itemId).getName() + " from cosmetic offers!");
                            CosmeticManager.onPurchase(player, offer.rewards.itemId, cost);
                        } else {
                            player.sendMessage("@red@You don't have enough credits for this item.");
                        }
                        p.getPA().closeAllWindows();
                    }),
                    new DialogueOption("no", p -> p.getPA().closeAllWindows())));
            return true;
        }

        if (optionalOffer.isPresent()) {
            CosmeticOffer offer = optionalOffer.get();
            int cost = offer.cost;
            if (player.getCosmeticCredits() >= cost) {
                player.setCosmeticCredits(player.getCosmeticCredits() - cost);
                player.getItems().addItem(offer.rewards.itemId, offer.rewards.itemAmount);
                player.sendMessage("@red@You claimed " + ItemDef.forId(offer.rewards.itemId).getName() + " from cosmetic offers!");
                CosmeticManager.onPurchase(player, offer.rewards.itemId, cost);
                updateOffers(player);
            } else {
                player.sendMessage("@red@You don't have enough credits for this item.");
            }
            return true;
        }
        return false;
    }

    private static void addnewCosmeticOffer(Player player, int itemID, int itemAmount, int cost, int index) {
        CosmeticOffer offer = offers.get(index);

        offer.rewards.itemId = itemID;
        offer.rewards.itemAmount = itemAmount;
        offer.cost = cost;

        save();

        player.sendMessage("@red@You have just updated a cosmetic offer!");
        PlayerHandler.executeGlobalMessage("[<col=CC0000>News</col>] <col=255> New Cosmetic Items have been added @ ::deals!");
    }

    public static class CosmeticOffer {
        public int buttonId;
        public int cost;
        public Reward rewards;

        public CosmeticOffer(int buttonId, int cost, int itemID, int itemAmount) {
            this.buttonId = buttonId;
            this.cost = cost;
            this.rewards = new Reward(itemID,itemAmount);
        }
    }

    public static class Reward {
        private int itemId;
        private int itemAmount;

        public Reward(int itemId, int itemAmount) {
            this.itemId = itemId;
            this.itemAmount = itemAmount;
        }

        public int getId() {
            return itemId;
        }

        public int getAmount() {
            return itemAmount;
        }
    }

    @PostInit
    public static void load() {
        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
                logger.info("Cosmetic offers YAML file was missing, so a new one was created.");
                return;
            }

            Yaml yaml = new Yaml();
            try (FileReader fileReader = new FileReader(file)) {
                Map<String, List<Map<String, Object>>> yamlData = yaml.load(fileReader);
                if (yamlData != null && yamlData.containsKey("deals")) {
                    List<Map<String, Object>> dealList = yamlData.get("deals");
                    for (Map<String, Object> dealData : dealList) {
                        int buttonId = (int) dealData.get("buttonId");
                        int cost = (int) dealData.get("cost");
                        int itemId = (int) dealData.get("itemId");
                        int itemAmount = (int) dealData.get("itemAmount");
                        offers.add(new CosmeticOffer(buttonId, cost, itemId, itemAmount));
                    }
                    logger.info("Loaded " + offers.size() + " cosmetic offers from YAML file.");
                } else {
                    logger.info("No cosmetic offers found in YAML file.");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error loading cosmetic offers: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            FileWriter fileWriter = new FileWriter(file);

            Yaml yaml = new Yaml();
            List<Map<String, Object>> dealList = new ArrayList<>();
            for (CosmeticOffer offer : offers) {
                Map<String, Object> dealData = Map.of(
                        "buttonId", offer.buttonId,
                        "cost", offer.cost,
                        "itemId", offer.rewards.itemId,
                        "itemAmount", offer.rewards.itemAmount
                );
                dealList.add(dealData);
            }
            Map<String, List<Map<String, Object>>> yamlData = Map.of("deals", dealList);
            yaml.dump(yamlData, fileWriter);

            fileWriter.close();
            logger.info("Saved " + offers.size() + " cosmetic offers to YAML file.");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error saving cosmetic offers: " + e.getMessage());
        }
    }
}