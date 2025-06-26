package io.kyros.content.games.goodiebag;

import io.kyros.Server;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.GameItem;
import io.kyros.sql.logging.RareLootLog;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoodieBagController {

    private final Player player;

    private static final int INTERFACE_ID = 24203;
    private static final int BUTTON_START_ID = 24206;
    private static final int BUTTON_END_ID = 24225;
    private static final int BUTTON_CONFIRM_ID = 24226;
    private static final int RESET_INDEX = -2;
    private static final int NO_SELECTION_INDEX = -1;
    private static final int TOTAL_BUTTONS = 20;

    private int selectedButtonIndex = NO_SELECTION_INDEX;
    private GoodieBag container;
    private GameItem legendaryItem;
    private final List<GameItem> genericList;
    private GameItem prizeReward;
    private int goodieID = -1;


    public GoodieBagController(Player player) {
        this.player = player;
        this.genericList = new ArrayList<>();
    }

    public boolean handleButtons(int buttonID) {
        if (isPrizeButton(buttonID)) {
            if (player.getItems().getInventoryCount(goodieID) <= 0) {
                player.sendErrorMessage("You do not have anymore goodie bags!");
                return true;
            }
            selectButton(buttonID);
            return true;
        } else if (buttonID == BUTTON_CONFIRM_ID) {
            handleConfirmButton();
            return true;
        }
        return false;
    }

    private boolean isPrizeButton(int buttonID) {
        return buttonID >= BUTTON_START_ID && buttonID <= BUTTON_END_ID;
    }

    private void selectButton(int buttonID) {
        if (selectedButtonIndex == RESET_INDEX) {
            player.sendErrorMessage("You need to reset the card first!");
            return;
        }
        selectedButtonIndex = getButtonIndex(buttonID);
        player.sendMessage("@blu@You have selected prize #" + (selectedButtonIndex + 1) + ", press confirm to collect your prize!");
    }

    private void handleConfirmButton() {
        if (player.getItems().getInventoryCount(goodieID) <= 0) {
            player.sendErrorMessage("You do not have anymore goodie bags!");
            return ;
        }
        if (selectedButtonIndex == RESET_INDEX) {
            resetSelection();
        } else if (selectedButtonIndex == NO_SELECTION_INDEX) {
            player.sendErrorMessage("You need to choose a square first!");
        } else {
            handleRoll();
            player.getItems().deleteItem2(goodieID, 1);
        }
    }

    private void resetSelection() {
        selectedButtonIndex = NO_SELECTION_INDEX;
        legendaryItem = null;
        reloadInterface(true);
    }

    private void handlePrize() {
        prizeReward = genericList.get(selectedButtonIndex);
        if (Server.GoodieBagThreshold >= 10 && !prizeReward.equals(legendaryItem)) {
            swapWithLegendaryItem();
            Server.GoodieBagThreshold = 0;
        }

        if (goodieID == 33396) {
            player.PremiumPoints += 1;
            player.sendMessage("You've just gotten 1 Premium Point, you now have " + player.PremiumPoints + ", checkout the ::premiumshop");
            if (player.hasEquippedSomewhere(33393)) {
                player.PremiumPoints += 1;
                player.sendMessage("You've just gained 1 extra Premium Point, you now have " + player.PremiumPoints + ", checkout the ::premiumshop");
            }
        }

        reloadInterface(false);
        broadcastLegendaryWin(prizeReward);
        player.sendErrorMessage("You've just won " + prizeReward.getDef().getName() + " x " + prizeReward.getAmount());
        player.getItems().addItemUnderAnyCircumstance(prizeReward.getId(), prizeReward.getAmount());

        new RareLootLog(player.getDisplayName(), prizeReward.getDef().getName(), prizeReward.getId(), prizeReward.getAmount(), ItemDef.forId(goodieID).getName(), Misc.getTime()).submit();
        resetAfterPrize();
    }

    private void swapWithLegendaryItem() {
        prizeReward = new GameItem(legendaryItem.getId(), legendaryItem.getAmount());
        int legendaryIndex = genericList.indexOf(legendaryItem);
        Collections.swap(genericList, legendaryIndex, selectedButtonIndex);
        genericList.set(selectedButtonIndex, prizeReward);
    }

    private void broadcastLegendaryWin(GameItem reward) {
        if (reward.equals(legendaryItem)) {
            String message = new MessageBuilder()
                    .color(MessageColor.BLACK)
                    .text("[")
                    .color(MessageColor.GREEN)
                    .text("GoodieBag")
                    .color(MessageColor.BLACK)
                    .text("] ")
                    .color(MessageColor.BLUE)
                    .text(player.getDisplayName())
                    .color(MessageColor.PURPLE)
                    .text(" has just got a ")
                    .text(reward.getDef().getName())
                    .text(" x ")
                    .text(String.valueOf(reward.getAmount()))
                    .text(" from a GoodieBag, get yours at ::store!")
                    .build();
            PlayerHandler.executeGlobalMessage(message);
        }
    }

    private void resetAfterPrize() {
        player.getPA().sendString(24226, "Reset");
        genericList.clear();
        selectedButtonIndex = RESET_INDEX;
        Server.GoodieBagThreshold++;
    }

    private void handleRoll() {
        legendaryItem = getRandomLegendaryItem();
        fillPrizeList(container.getItems().getCommon(), TOTAL_BUTTONS - 1);
        genericList.add(legendaryItem);
        Collections.shuffle(genericList);
        handlePrize();
    }

    private GameItem getRandomLegendaryItem() {
        List<Card> legendaryItems = container.getItems().getLegendary();
        int randomIndex = Misc.random(legendaryItems.size() - 1);
        return new GameItem(legendaryItems.get(randomIndex).getId(), legendaryItems.get(randomIndex).getAmount());
    }

    private void fillPrizeList(List<Card> items, int count) {
        for (int i = 0; i < count; i++) {
            int rng = Misc.random(items.size() - 1);
            GameItem item = new GameItem(items.get(rng).getId(), items.get(rng).getAmount());
            genericList.add(item);
        }
    }

    private int getButtonIndex(int buttonId) {
        return buttonId - BUTTON_START_ID;
    }

    public void showInterface(GoodieBag goodieBag, int itemId) {
        resetSelection();
        this.container = goodieBag;
        reloadInterface(true);
        player.getPA().sendString(INTERFACE_ID + 2, ItemDef.forId(itemId).getName());
        player.getPA().showInterface(INTERFACE_ID);
        goodieID = itemId;
    }

    private void reloadInterface(boolean reset) {
        if (reset) {
            resetInterface();
        } else {
            updateInterfaceWithPrizes();
        }
    }

    private void resetInterface() {
        player.getPA().sendString(24226, "Confirm");
        for (int i = 0; i < TOTAL_BUTTONS; i++) {
            player.getPA().itemOnInterface(-1, 1, 24227, i);
            player.getPA().sendString(BUTTON_START_ID + i, String.valueOf(i + 1));
        }
    }

    private void updateInterfaceWithPrizes() {
        for (int i = 0; i < genericList.size(); i++) {
            GameItem item = genericList.get(i);
            player.getPA().itemOnInterface(item.getId(), item.getAmount(), 24227, i);
            player.getPA().sendString(BUTTON_START_ID + i, ""); // Clear numbers
        }
        player.getPA().showInterface(INTERFACE_ID);
    }
}