package io.kyros.content.event_manager;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.event_manager.Event;
import io.kyros.content.event_manager.EventType;
import io.kyros.model.SlottedItem;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.GameItem;

public class EventManager {

    public static final int INTERFACE_ID = 75_000;

    public static int INVENTORY_SCREEN_ID = 77_998;

    public static int INVENTORY_CONTAINER_ID = 77_999;

    public static Event lastStartedEvent = null;

    public static void open(Player player) {

        displayEvent(player, EventType.DIG_EVENT.getEvent());

        updateInventory(player);

        for (int ii = 0; ii < 141; ii++) {
            player.getPA().itemOnInterface(-1, 1, INTERFACE_ID+75+1, ii);
        }

        for (int i = 0; i < EventType.DIG_EVENT.getEvent().getEventRewards().size(); i++) {
            GameItem item = EventType.DIG_EVENT.getEvent().getEventRewards().get(i);
            player.getPA().itemOnInterface(item.getId(), item.getAmount(), INTERFACE_ID + 75 + 1, i);
        }

        player.getPA().showInterface(INTERFACE_ID);
        player.setSidebarInterface(3, INVENTORY_SCREEN_ID);
    }

    public static void closeInterface(Player player) {
        if(player.getOpenInterface() != 75_000 && player.viewingEvent != null) {
            player.setSidebarInterface(3, 3213);
            player.viewingEvent = null;
        }
    }

    public static void displayEvent(Player player, Event event) {
        player.viewingEvent = event;

        player.getPA().sendString(INTERFACE_ID+4, event.getEventType().getName());

        int childId = INTERFACE_ID+7;

        for(int i = 0; i < 4; i++) {
            String setting = event.getSetting(i);

            if(setting != null) {
                setting += ": @whi@" + event.getSettingValue(i);
            }
            player.getPA().sendString(childId++, setting == null ? "" : setting);
        }

        childId = INTERFACE_ID+11;
        for(int i = 0; i < 4; i++) {
            String setting = event.getSetting(i);
            player.getPA().sendInterfaceHidden(childId++, setting == null);
        }

        for (int i = 0; i < EventType.DIG_EVENT.getEvent().getEventRewards().size(); i++) {
            GameItem item = EventType.DIG_EVENT.getEvent().getEventRewards().get(i);
            player.getPA().itemOnInterface(item.getId(), item.getAmount(), INTERFACE_ID + 75 + 1, i);
        }

    }

    public static boolean isButton(Player player, int buttonId) {
        if(!player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            return false;
        }
        if(buttonId >= INTERFACE_ID+11 && buttonId <= INTERFACE_ID+14) {
            int index = Math.abs(INTERFACE_ID+11 - buttonId);

            player.getPA().sendEnterAmount("Enter the value for "+player.viewingEvent.getSetting(index)+":", (player1, amount) -> {
                player.viewingEvent.setSetting(index, amount);
                displayEvent(player, player.viewingEvent);
            });
            return true;
        }

        if(buttonId == INTERFACE_ID+3) {
            player.getPA().closeAllWindows();
            return true;
        }

        if(buttonId == INTERFACE_ID+5) {
            player.viewingEvent.startEvent(player);
            return true;
        }

        if(buttonId == INTERFACE_ID+6) {
            player.viewingEvent.endEvent(player);
            return true;
        }

        return false;
    }

    public static boolean handleDropDown(Player player, int widgetId, int index) {
        if(widgetId == 75_015) {
            displayEvent(player, EventType.values()[index].getEvent());
            return true;
        }
        return false;
    }

    public static void add(Player player, int itemId, int amount) {
        Event viewingEvent = player.viewingEvent;

        if(player.getItems().getInventoryCount(itemId) < amount) {
            return;
        }

        if(viewingEvent.getEventRewards().size() >= 100) {
            player.sendMessage("You can only add 100 rewards to the Event.");
            return;
        }

        player.getItems().deleteItem2(itemId, amount);
        viewingEvent.getEventRewards().add(new GameItem(itemId, amount));

        for (int i = 0; i < viewingEvent.getEventRewards().size(); i++) {
            GameItem item = viewingEvent.getEventRewards().get(i);
            player.getPA().itemOnInterface(item.getId(), item.getAmount(), INTERFACE_ID + 75 + 1, i);
        }
        updateInventory(player);

        player.getPA().showInterface(INTERFACE_ID);
        player.setSidebarInterface(3, INVENTORY_SCREEN_ID);
    }

    public static void remove(Player player, int itemId, int amount, int slot) {
        Event viewingEvent = player.viewingEvent;

        if(!player.getRights().isOrInherits(Right.ADMINISTRATOR)) {
            return;
        }

        amount = viewingEvent.getEventRewards().get(slot).getAmount();

        int currentSlot = 0;

        for(GameItem item : viewingEvent.getEventRewards()) {
            if(item == null)
                continue;

            if(currentSlot == slot) {
                if(item.getAmount() <= amount) {
                    player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
                    viewingEvent.getEventRewards().remove(slot);

                } else {
                    player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
                    item.setAmount(item.getAmount() - amount);
                }
                break;
            }

            currentSlot++;
        }

        updateInventory(player);

        player.getPA().showInterface(INTERFACE_ID);
        player.setSidebarInterface(3, INVENTORY_SCREEN_ID);
    }

    public static void updateInventory(Player player) {
        player.getItems().sendInventoryInterface(INVENTORY_CONTAINER_ID);
    }
}
