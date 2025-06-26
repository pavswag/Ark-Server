package io.kyros.content.item.lootable;

import io.kyros.Configuration;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.sql.logging.RareLootLog;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.List;
import java.util.Random;

public abstract class MysteryBoxLootable implements Lootable {

    public abstract int getItemId();

    private final Player player;
    public boolean canMysteryBox = true;
    private int mysteryPrize;
    private int mysteryAmount;
    private int spinNum;
    @Getter
    private boolean active;
    private final int INTERFACE_ID = 47000;
    private final int ITEM_FRAME = 47101;

    public MysteryBoxLootable(Player player) {
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

    public void setMysteryPrize() {
        double[] rarityProbabilities = {0.02, 0.12, 0.39, 0.90};
        double random = Math.random();

        List<GameItem> itemList = null;

        if (getItemId() == 19897 || getItemId() == 28094) {
            if (random < rarityProbabilities[0] && Configuration.GLOBAL_BOX_COUNT == 0) {
                itemList = getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity());
            } else if (random < rarityProbabilities[1]) {
                itemList = getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
            } else if (random < rarityProbabilities[2]) {
                itemList = getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity());
            } else if (random < rarityProbabilities[3]) {
                itemList = getLoot().get(MysteryBoxRarity.COMMON.getLootRarity());
            }

            if (itemList != null) {
                GameItem item = Misc.getRandomItem(itemList);
                mysteryPrize = item.getId();
                mysteryAmount = item.getAmount();
            } else {
                mysteryPrize = getItemId();
                mysteryAmount = 1;
            }

            Configuration.GLOBAL_BOX_COUNT--;
            if (Configuration.GLOBAL_BOX_COUNT == 0) {
                Configuration.GLOBAL_BOX_COUNT = 500;
            }

        } else {
            if (random < rarityProbabilities[0]) {
                itemList = getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity());
            } else if (random < rarityProbabilities[1]) {
                itemList = getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
            } else if (random < rarityProbabilities[2]) {
                itemList = getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity());
            } else if (random < rarityProbabilities[3]) {
                itemList = getLoot().get(MysteryBoxRarity.COMMON.getLootRarity());
            }

            if (itemList != null) {
                GameItem item = Misc.getRandomItem(itemList);
                mysteryPrize = item.getId();
                mysteryAmount = item.getAmount();
            } else {
                mysteryPrize = getItemId();
                mysteryAmount = 1;
            }
        }
    }

    public void sendItem(int i, int prizeSlot, int prizeId, int notPrizeId, int amount) {
        int itemId = (i == prizeSlot) ? prizeId : notPrizeId;
        player.getPA().mysteryBoxItemOnInterface(itemId, amount, ITEM_FRAME, i);
    }

    public void openInterface() {
        player.boxCurrentlyUsing = getItemId();
        for (int i = 0; i < 66; i++) {
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.getPA().sendString(ItemDef.forId(getItemId()).getName(), 47002);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public void canMysteryBox() {
        canMysteryBox = true;
    }

    public void quickOpen() {
        if (player.getUltraInterface().isActive() ||
                player.getSuperBoxInterface().isActive() ||
                player.getNormalBoxInterface().isActive() ||
                player.getFoeInterface().isActive() ||
                player.getChristmasInterface().isActive() ||
                player.getF2pDivisionBox().isActive() ||
                player.getP2pDivisionBox().isActive() ||
                player.getAncientCasket().isActive() ||
                player.getCoxBox().isActive() ||
                player.getArboBox().isActive() ||
                player.getTobBox().isActive() ||
                player.getDonoBox().isActive() ||
                player.getCosmeticBox().isActive() ||
                player.getMiniArboBox().isActive() ||
                player.getMiniCoxBox().isActive() ||
                player.getMiniDonoBox().isActive() ||
                player.getMiniNormalMysteryBox().isActive() ||
                player.getMiniSmb().isActive() ||
                player.getMiniTobBox().isActive() ||
                player.getBounty7().isActive() ||
                player.getMiniUltraBox().isActive() ||
                player.getWonderBox().isActive() ||
                player.getSupriseBox().isActive() ||
                player.getGreatPhantomBox().isActive() ||
                player.getPhantomBox().isActive() ||
                player.getSuperVoteBox().isActive() ||
                player.getBisBox().isActive() ||
                player.getChaoticBox().isActive() ||
                player.getCrusadeBox().isActive()  ||
                player.getFreedomBox().isActive()   ||
                player.getMiniShadowRaidBox().isActive() ||
                player.getShadowRaidBox().isActive() ){

            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }

        if (!player.canRollBox(player)) {
            player.getPA().showInterface(47000);
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }

        if (!player.getItems().hasItemOnOrInventory(getItemId())) {
            return;
        }

        int amount = player.getItems().getInventoryCount(getItemId());
        amount = Math.min(amount, 1000);

        for (int i = 0; i < amount; i++) {
            if (player.getItems().playerHasItem(getItemId(), 1)) {
                player.getItems().deleteItem(getItemId(), 1);
                setMysteryPrize();
                roll(player);
            } else {
                player.sendMessage("@blu@You have used your last mystery box.");
                break;
            }
        }
    }

    @Override
    public void roll(Player player) {
        if (mysteryPrize == -1) {
            canMysteryBox = true;
            player.getNormalMysteryBox().canMysteryBox();
            player.getUltraMysteryBox().canMysteryBox();
            player.getSuperMysteryBox().canMysteryBox();
            player.getFoeMysteryBox().canMysteryBox();
            player.getYoutubeMysteryBox().canMysteryBox();
            player.getChristmasBox().canMysteryBox();
            player.getF2pDivisionBox().canMysteryBox();
            player.getP2pDivisionBox().canMysteryBox();
            player.getAncientCasket().canMysteryBox();
            player.getArboBox().canMysteryBox();
            player.getCoxBox().canMysteryBox();
            player.getTobBox().canMysteryBox();
            player.getDonoBox().canMysteryBox();
            player.getCosmeticBox().canMysteryBox();
            player.getMiniArboBox().canMysteryBox();
            player.getMiniCoxBox().canMysteryBox();
            player.getMiniDonoBox().canMysteryBox();
            player.getMiniNormalMysteryBox().canMysteryBox();
            player.getMiniSmb().canMysteryBox();
            player.getMiniTobBox().canMysteryBox();
            player.getMiniUltraBox().canMysteryBox();
            player.getBounty7().canMysteryBox();
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
        active = false;
        player.inDonatorBox = true;
        canMysteryBox = true;
        mysteryPrize = -1;
        player.getNormalMysteryBox().canMysteryBox();
        player.getUltraMysteryBox().canMysteryBox();
        player.getSuperMysteryBox().canMysteryBox();
        player.getFoeMysteryBox().canMysteryBox();
        player.getYoutubeMysteryBox().canMysteryBox();
        player.getChristmasBox().canMysteryBox();
        player.getF2pDivisionBox().canMysteryBox();
        player.getP2pDivisionBox().canMysteryBox();
        player.getAncientCasket().canMysteryBox();
        player.getArboBox().canMysteryBox();
        player.getCoxBox().canMysteryBox();
        player.getTobBox().canMysteryBox();
        player.getDonoBox().canMysteryBox();
        player.getCosmeticBox().canMysteryBox();
        player.getMiniArboBox().canMysteryBox();
        player.getMiniCoxBox().canMysteryBox();
        player.getMiniDonoBox().canMysteryBox();
        player.getMiniNormalMysteryBox().canMysteryBox();
        player.getMiniSmb().canMysteryBox();
        player.getMiniTobBox().canMysteryBox();
        player.getMiniUltraBox().canMysteryBox();
        player.getBounty7().canMysteryBox();
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
    }
}
