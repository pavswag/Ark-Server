package io.kyros.content.item.lootable;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.sql.logging.RareLootLog;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.*;

public abstract class MysteryBoxLootableNew implements Lootable {

    public abstract int getItemId();
    public abstract Map<LootRarity, Integer> getRates();

    private final Player player;
    public boolean canMysteryBox = true;
    private int mysteryPrize;
    private int mysteryAmount;
    private int spinNum;
    @Getter
    private boolean active;
    private final int INTERFACE_ID = 23723;
    private final int ITEM_FRAME = 47101;

    public MysteryBoxLootableNew(Player player) {
        this.player = player;
    }

    public void spin() {
        if (!canMysteryBox) {
            player.sendMessage("Please finish your current spin.");
            return;
        }
        if (!player.getItems().playerHasItem(getItemId())) {
            player.sendMessage("You require a mystery box to do this.");
            return;
        }

        player.getItems().deleteItem(getItemId(), 1);
        player.sendMessage(":resetBox");
        for (int i = 0; i < 66; i++) {
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.sendMessage(":spin");
        process();
    }

    public void process() {
        player.getPA().closeAllWindows();
        mysteryPrize = -1;
        mysteryAmount = -1;
        canMysteryBox = false;
        active = true;
        setMysteryPrize();

        if (mysteryPrize == -1) return;

        if (spinNum == 0) {
            for (int i = 0; i < 66; i++) {
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                GameItem notPrize = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity()));
                sendItem(i, 55, mysteryPrize, notPrize.getId(), 1);
            }
        } else {
            for (int i = spinNum * 50 + 16; i < spinNum * 50 + 66; i++) {
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                int notPrizeId = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity())).getId();
                sendItem(i, (spinNum + 1) * 50 + 5, mysteryPrize, notPrizeId, mysteryAmount);
            }
        }

        player.getPA().showInterface(INTERFACE_ID);
        spinNum++;
    }

    int commonCounter = 0;
    int uncommonCounter = 0;
    int rareCounter = 0;
    int veryRareCounter = 0;

    public void setMysteryPrize() {
//        System.out.println("Starting setMysteryPrize method");

        // Check if loot is null or empty and handle accordingly
        if (getLoot() == null || getLoot().isEmpty()) {
//            player.sendMessage("Loot is not available for this box.");
//            System.out.println("Loot is not available, exiting method.");
            return;
        }

        int totalRate = 10000; // Total probability space for 1/10,000 system
        int randomValue = Misc.random(totalRate);

//        System.out.println("Generated randomValue = " + randomValue);

        int cumulativeRate = 0;
        LootRarity selectedRarity = LootRarity.COMMON; // default to common

        // Check for Very Rare
        if (getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity()).isEmpty()) {
            cumulativeRate += getRates().getOrDefault(LootRarity.VERY_RARE, 0);
//            System.out.println("Checking Very_Rare: cumulativeRate = " + cumulativeRate);
            if (randomValue < cumulativeRate) {
                selectedRarity = LootRarity.VERY_RARE;
                veryRareCounter++; // Increment Very Rare counter
//                System.out.println("Selected Rarity = Very_Rare");
            }
        }

        // Check for Rare
        if (selectedRarity == LootRarity.COMMON && getLoot().get(MysteryBoxRarity.RARE.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.RARE.getLootRarity()).isEmpty()) {
            cumulativeRate += getRates().getOrDefault(LootRarity.RARE, 0);
//            System.out.println("Checking Rare: cumulativeRate = " + cumulativeRate);
            if (randomValue < cumulativeRate) {
                selectedRarity = LootRarity.RARE;
                rareCounter++; // Increment Rare counter
//                System.out.println("Selected Rarity = Rare");
            }
        }

        // Check for Uncommon
        if (selectedRarity == LootRarity.COMMON && getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity()).isEmpty()) {
            cumulativeRate += getRates().getOrDefault(LootRarity.UNCOMMON, 0);
//            System.out.println("Checking Uncommon: cumulativeRate = " + cumulativeRate);
            if (randomValue < cumulativeRate) {
                selectedRarity = LootRarity.UNCOMMON;
                uncommonCounter++; // Increment Uncommon counter
//                System.out.println("Selected Rarity = Uncommon");
            }
        }

        // If no other rarity was selected, count as Common
        if (selectedRarity == LootRarity.COMMON) {
            commonCounter++; // Increment Common counter
        }

        // Final selected rarity
//        System.out.println("Final selected rarity after checks: " + selectedRarity);

        // Fallback to COMMON if no other rarity was selected
        List<GameItem> itemList = getLoot().getOrDefault(selectedRarity, getLoot().get(LootRarity.COMMON));
        if (itemList == null || itemList.isEmpty()) {
//            player.sendMessage("No items available for this rarity.");
//            System.out.println("No items available for the selected rarity, exiting method.");
            return;
        }

        GameItem item = Misc.getRandomItem(itemList);
        mysteryPrize = item.getId();
        mysteryAmount = item.getAmount();

//        System.out.println("Selected item ID: " + mysteryPrize + ", amount: " + mysteryAmount);
//        System.out.println("Ending setMysteryPrize method");
//        System.out.println("Current totals - Common: " + commonCounter + ", Uncommon: " + uncommonCounter + ", Rare: " + rareCounter + ", Very Rare: " + veryRareCounter);
    }

    public void sendItem(int i, int prizeSlot, int prizeId, int notPrizeId, int amount) {
        int itemId = (i == prizeSlot) ? prizeId : notPrizeId;
        player.getPA().mysteryBoxItemOnInterface(itemId, amount, ITEM_FRAME, i);
    }

    public void handleItemList() {
        player.sendMessage(":clearItems");

        List<GameItem> itemList = new ArrayList<>();
        Set<Integer> itemSet = new HashSet<>();

        // Check if loot is null or empty and handle accordingly
        if (getLoot() == null || getLoot().isEmpty()) {
//            player.sendMessage("No loot available to display.");
            return;
        }

        // Process only non-null and non-empty rarity lists
        if (getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity()).isEmpty()) {
            List<GameItem> veryRareItems = getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity());
            addItemsToList(veryRareItems, itemList, itemSet);
        }
        if (getLoot().get(MysteryBoxRarity.RARE.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.RARE.getLootRarity()).isEmpty()) {
            List<GameItem> rareItems = getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
            addItemsToList(rareItems, itemList, itemSet);
        }
        if (getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity()).isEmpty()) {
            List<GameItem> uncommonItems = getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity());
            addItemsToList(uncommonItems, itemList, itemSet);
        }
        if (getLoot().get(MysteryBoxRarity.COMMON.getLootRarity()) != null && !getLoot().get(MysteryBoxRarity.COMMON.getLootRarity()).isEmpty()) {
            List<GameItem> commonItems = getLoot().get(MysteryBoxRarity.COMMON.getLootRarity());
            addItemsToList(commonItems, itemList, itemSet);
        }

        // Display items on the interface
        for (int i = 0; i < itemList.size(); i++) {
            int itemId = itemList.get(i).getId();
            int itemAmount = itemList.get(i).getAmount();
            player.getPA().itemOnInterface(itemId, itemAmount, 23732, i);
        }
    }

    private void addItemsToList(List<GameItem> items, List<GameItem> itemList, Set<Integer> itemSet) {
        for (GameItem item : items) {
            if (!itemSet.contains(item.getId())) {
                itemList.add(item);
                itemSet.add(item.getId());
            }
        }
    }

    public void openInterface() {
        player.boxCurrentlyUsing = getItemId();
        for (int i = 0; i < 66; i++) {
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }

        handleItemList();
        handleRakeBackShow(player);

        spinNum = 0;
        player.getPA().sendString(ItemDef.forId(getItemId()).getName(), 23725);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public void canMysteryBox() {
        canMysteryBox = true;
    }

    public void quickSpin() {
        if (
                player.getMiniNormalMysteryBox().isActive() ||
                        player.getNormalMysteryBox().isActive() ||
                        player.getMiniSmb().isActive() ||
                        player.getSuperMysteryBox().isActive() ||
                        player.getMiniUltraBox().isActive() ||
                        player.getUltraMysteryBox().isActive() ||
                        player.getMiniCoxBox().isActive() ||
                        player.getCoxBox().isActive() ||
                        player.getMiniTobBox().isActive() ||
                        player.getTobBox().isActive() ||
                        player.getMiniArboBox().isActive() ||
                        player.getArboBox().isActive() ||
                        player.getMiniDonoBox().isActive() ||
                        player.getDonoBox().isActive() ||
                        player.getFoeMysteryBox().isActive() ||
                        player.getF2pDivisionBox().isActive() ||
                        player.getP2pDivisionBox().isActive() ||
                        player.getWonderBox().isActive() ||
                        player.getSupriseBox().isActive() ||
                        player.getGreatPhantomBox().isActive() ||
                        player.getPhantomBox().isActive() ||
                        player.getSuperVoteBox().isActive() ||
                        player.getBisBox().isActive() ||
                        player.getChaoticBox().isActive() ||
                        player.getCrusadeBox().isActive() ||
                        player.getFreedomBox().isActive() ||
                        player.getMiniShadowRaidBox().isActive() ||
                        player.getShadowRaidBox().isActive()  ||
                        player.getHereditBox().isActive()  ||
                        player.getDamnedBox().isActive() ||
                        player.getForsakenBox().isActive() ||
                        player.getBoxes().isActive() ||
                        player.getTumekensBox().isActive() ||
                        player.getJudgesBox().isActive()  ||
                        player.getXamphurBox().isActive()  ||
                        player.getMinotaurBox().isActive() ){
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }

        if (player.hitStandardRateLimit(true))
            return;

        if (!player.canRollBox(player)) {
            player.getPA().showInterface(INTERFACE_ID);
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }

        if (!player.getItems().hasItemOnOrInventory(getItemId())) {
            return;
        }

        int amount = player.getItems().getInventoryCount(getItemId());
        amount = Math.min(amount, 1_000);

        int finalAmount = amount;


        new Thread() {
            @Override
            public void run() {
                List<GameItem> rewards = new ArrayList<>();
                int rakeBackProgress = 0;
                for(int i = 0; i < finalAmount; i++) {
                    if (player.getItems().playerHasItem(getItemId(), 1)) {
                        setMysteryPrize();
                        rewards.add(new GameItem(mysteryPrize, mysteryAmount));
                        rakeBackProgress++; // Track how many times rake back should progress
                    } else {
                        break;
                    }
                }
                int finalRakeBackProgress = rakeBackProgress;
                player.queue(() -> {
                    if(player.getItems().playerHasItem(getItemId(), finalAmount)) {
                        player.getItems().deleteItem(getItemId(), finalAmount);
                        player.getItems().addItemsBatch(rewards);

                        // Handle rake back progress
                        for (int i = 0; i < finalRakeBackProgress; i++) {
                            handleRakeBack(player);
                        }

                        player.sendMessage("You've generated " + rewards.size() + " rewards. They've been sent to your inventory or bank.");
                    }
                });
            }
        }.start();
    }

    @Override
    public void roll(Player player) {
        if (mysteryPrize == -1) {
            canMysteryBox = true;
            player.getMiniNormalMysteryBox().canMysteryBox();
            player.getNormalMysteryBox().canMysteryBox();
            player.getMiniSmb().canMysteryBox();
            player.getSuperMysteryBox().canMysteryBox();
            player.getMiniUltraBox().canMysteryBox();
            player.getUltraMysteryBox().canMysteryBox();
            player.getMiniCoxBox().canMysteryBox();
            player.getCoxBox().canMysteryBox();
            player.getMiniTobBox().canMysteryBox();
            player.getTobBox().canMysteryBox();
            player.getMiniArboBox().canMysteryBox();
            player.getArboBox().canMysteryBox();
            player.getMiniDonoBox().canMysteryBox();
            player.getDonoBox().canMysteryBox();
            player.getFoeMysteryBox().canMysteryBox();
            player.getF2pDivisionBox().canMysteryBox();
            player.getP2pDivisionBox().canMysteryBox();
            player.getWonderBox().canMysteryBox();
            player.getSupriseBox().canMysteryBox();
            player.getGreatPhantomBox().canMysteryBox();
            player.getPhantomBox().canMysteryBox();
            player.getSuperVoteBox().canMysteryBox();
            player.getBisBox().canMysteryBox();
            player.getChaoticBox().canMysteryBox();
            player.getCrusadeBox().canMysteryBox();
            player.getFreedomBox().canMysteryBox();
            player.getMiniShadowRaidBox().canMysteryBox();
            player.getShadowRaidBox().canMysteryBox();
            player.getHereditBox().canMysteryBox();
            player.getDamnedBox().canMysteryBox();
            player.getBoxes().canMysteryBox();
            player.getTumekensBox().canMysteryBox();
            player.getJudgesBox().canMysteryBox();
            player.getXamphurBox().canMysteryBox();
            player.getMinotaurBox().canMysteryBox();
            return;
        }

        int amt = (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33113) && Misc.random(0, 100) < 10 ? mysteryAmount * 2 : mysteryAmount);

        if (player.hasFollower && player.petSummonId == 33067 && Misc.random(0, 100) >= 90 || player.getItems().playerHasItem(33067) && Misc.random(0, 100) >= 90) {
            amt *= 2;
        }

        if (player.EliteCentBoost > 0 && Misc.random(0, 100) >= (player.centurion == 52 ? 85 : player.centurion == 53 ? 80 : player.centurion == 57 ? 70 : player.centurion == 56 ? 65 : 90)) {
            amt *= 2;
        }

        player.getItems().addItemUnderAnyCircumstance(mysteryPrize, amt);

        new RareLootLog(player.getDisplayName(), ItemDef.forId(mysteryPrize).getName(), mysteryPrize, amt, ItemDef.forId(getItemId()).getName(), Misc.getTime()).submit();

        handleRakeBack(player);

        active = false;
        player.inDonatorBox = true;

        canMysteryBox = true;

        mysteryPrize = -1;
        player.getMiniNormalMysteryBox().canMysteryBox();
        player.getNormalMysteryBox().canMysteryBox();
        player.getMiniSmb().canMysteryBox();
        player.getSuperMysteryBox().canMysteryBox();
        player.getMiniUltraBox().canMysteryBox();
        player.getUltraMysteryBox().canMysteryBox();
        player.getMiniCoxBox().canMysteryBox();
        player.getCoxBox().canMysteryBox();
        player.getMiniTobBox().canMysteryBox();
        player.getTobBox().canMysteryBox();
        player.getMiniArboBox().canMysteryBox();
        player.getArboBox().canMysteryBox();
        player.getMiniDonoBox().canMysteryBox();
        player.getDonoBox().canMysteryBox();
        player.getFoeMysteryBox().canMysteryBox();
        player.getF2pDivisionBox().canMysteryBox();
        player.getP2pDivisionBox().canMysteryBox();
        player.getWonderBox().canMysteryBox();
        player.getSupriseBox().canMysteryBox();
        player.getGreatPhantomBox().canMysteryBox();
        player.getPhantomBox().canMysteryBox();
        player.getSuperVoteBox().canMysteryBox();
        player.getBisBox().canMysteryBox();
        player.getChaoticBox().canMysteryBox();
        player.getCrusadeBox().canMysteryBox();
        player.getFreedomBox().canMysteryBox();
        player.getMiniShadowRaidBox().canMysteryBox();
        player.getShadowRaidBox().canMysteryBox();
        player.getHereditBox().canMysteryBox();
        player.getDamnedBox().canMysteryBox();
        player.getBoxes().canMysteryBox();
        player.getTumekensBox().canMysteryBox();
        player.getJudgesBox().canMysteryBox();
        player.getXamphurBox().canMysteryBox();
        player.getMinotaurBox().canMysteryBox();
    }

    public void handleRakeBack(Player player) {
        // Execute the RakeBack logic in sync with the game thread
        player.queue(() -> {
            // Increment progress for the item
            player.getRakeBackSystem().merge(getItemId(), 1, Integer::sum);

            // Check if the player completed the RakeBack system
            if (player.getRakeBackSystem().get(getItemId()) >= 12) {
//                player.sendErrorMessage("You've completed the RakeBackSystem and have been rewarded for your efforts!");
                player.getRakeBackSystem().put(getItemId(), 0);
                player.getItems().addItemUnderAnyCircumstance(getItemId(), 1);
            }

            // Update the player's interface
            handleRakeBackShow(player);
        });
    }

    public void handleRakeBackShow(Player player) {
        // Update the RakeBack interface
        int id = 23733;
        for (int i = 0; i < 12; i++) {
            player.getPA().sendChangeSprite(id + i, (byte) 4);
        }

        // Update based on the player's progress
        if (player.getRakeBackSystem().containsKey(getItemId())) {
            for (int i = 0; i < player.getRakeBackSystem().get(getItemId()); i++) {
                player.getPA().sendChangeSprite(id + i, (byte) 8);
            }
            player.getPA().sendString(23728, "Current progress " + player.getRakeBackSystem().get(getItemId()) + "/12");
        } else {
            player.getPA().sendString(23728, "Current progress 0/12");
        }

        player.getPA().sendString(23725, ItemDef.forId(getItemId()).getName());
    }

}
