package io.kyros.content.event_manager.impl;



import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.event_manager.Event;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.event_manager.EventType;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DigEvent extends Event {

    public ArrayList<Position> digPositions = new ArrayList<>();

    public DigEvent() {
        this.setSetting(0, 10);
    }

    public String getSetting(int index) {
        switch(index) {
            case 0:
                return "Dig Radius";
        }
        return null;
    }

    public EventType getEventType() {
        return EventType.DIG_EVENT;
    }

    public void startEventDialogues(Player player) {
        player.start(new DialogueBuilder(player).statement("Welcome to the Dig Event!\\n dig around this area for a chance to find loot!\\n There's "+
                EventManager.lastStartedEvent.getEventRewards().size()+" total possible loots."));
    }


    public void startEvent(Player player) {
        super.startEvent(player);

        this.digPositions.clear();

        int currentX = player.getPosition().getX();
        int currentY = player.getPosition().getY();

        int radius = getSettingValue(0);

        ArrayList<Position> possibleDigPositions = new ArrayList<>();

        for(int xPos = currentX - radius; xPos < currentX + radius; xPos++) {
            for(int yPos = currentY - radius; yPos < currentY + radius; yPos++) {
                if (!RegionProvider.getGlobal().isBlocked(xPos, yPos, 0)) {
                    possibleDigPositions.add(new Position(xPos, yPos));
                }
            }
        }

        for(GameItem item : getEventRewards()) {
            Position randomPosition = possibleDigPositions.get(Misc.random(possibleDigPositions.size() - 1));
            digPositions.add(randomPosition);
        }

    }

    @Override
    public void dig(Player player, int itemID) {
        Event event = EventManager.lastStartedEvent;

        player.startAnimation(itemID == 27873 ? 10045 : 830);

        CopyOnWriteArrayList<Position> digPositionsCopy = new CopyOnWriteArrayList<>(digPositions);

        List<Position> positionsToCheck = new ArrayList<>();

        if (itemID == 27873) {
            Position playerPos = player.getPosition();
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    positionsToCheck.add(new Position(playerPos.getX() + xOffset, playerPos.getY() + yOffset, playerPos.getHeight()));
                }
            }
        } else {
            positionsToCheck.add(player.getPosition());
        }

        for (Position position : digPositionsCopy) {
            if (positionsToCheck.contains(position)) {
                GameItem randomItem = event.getEventRewards().get(Misc.random(event.getEventRewards().size() - 1));
                player.getItems().addItemUnderAnyCircumstance(randomItem.getId(), randomItem.getAmount());
                player.start(new DialogueBuilder(player).statement("You have found an item! " + randomItem.getDef().getName()));
                event.getEventRewards().remove(randomItem);
                digPositions.remove(position);

                PlayerHandler.executeGlobalMessage("<shad=fa2a55><img=10> [" + getEventType().getName() + "] " +
                        player.getDisplayName() + " found " + randomItem.getAmount() + "x " + ItemDef.forId(randomItem.getId()).getName() +
                        "! There are " + event.getEventRewards().size() + " items left to be found.");
                break;
            }
        }

        if (digPositions.isEmpty()) {
            endEvent(player);
        }
    }


}
