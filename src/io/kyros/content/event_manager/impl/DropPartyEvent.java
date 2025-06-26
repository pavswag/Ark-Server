package io.kyros.content.event_manager.impl;

import io.kyros.Server;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.event_manager.Event;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.event_manager.EventType;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DropPartyEvent extends Event {

    private static final List<Position> possibleDropPositions = new ArrayList<>();
    private static final HashMap<String, Integer> itemsLooted = new HashMap<>();
    private static final List<GlobalObject> balloonsPopped = new ArrayList<>();
    private static final HashMap<Position, GameItem> balloonContents = new HashMap<>();



    public DropPartyEvent() {
        this.setSetting(0, 10); // Drop Radius
        this.setSetting(1, 10); // Max Loots
    }

    @Override
    public String getSetting(int index) {
        return switch (index) {
            case 0 -> "Drop Radius";
            case 1 -> "Max Loots";
            default -> null;
        };
    }

    @Override
    public EventType getEventType() {
        return EventType.DROP_PARTY;
    }

    @Override
    public void startEventDialogues(Player player) {
        player.start(new DialogueBuilder(player).statement(
                "Welcome to the Drop Party Event!\n" +
                        "Items will be dropped around this area!\n" +
                        "There are " + EventManager.lastStartedEvent.getEventRewards().size() + " total possible loots.\n"
        ));
    }

    @Override
    public void endEvent(Player player) {
        super.endEvent(player);
        itemsLooted.clear();
        possibleDropPositions.clear();
        balloonsPopped.clear();
    }

    @Override
    public void startEvent(Player player) {
        super.startEvent(player);

        itemsLooted.clear();
        possibleDropPositions.clear();
        balloonsPopped.clear();

        int lowX = 3037;
        int lowY = 3372;
        int highX = 3054;
        int highY = 3384;

        for (int xPos = lowX; xPos <= highX; xPos++) {
            for (int yPos = lowY; yPos <= highY; yPos++) {
                if (!RegionProvider.getGlobal().isBlocked(xPos, yPos, 0) && !Server.getGlobalObjects().anyBalloonsExists(xPos, yPos, 0)) {
                    possibleDropPositions.add(new Position(xPos, yPos));
                }
            }
        }
    }

    public void startActions() {
        CopyOnWriteArrayList<GameItem> eventItems = new CopyOnWriteArrayList<>(getEventRewards());
        balloonContents.clear();  // Clear the map before starting

        int maxItems = eventItems.size();
        List<Position> availablePositions = new ArrayList<>(possibleDropPositions);
        Collections.shuffle(availablePositions);  // Shuffle to ensure random order

//        System.out.println("Total event items: " + maxItems);

        for (int i = 0; i < availablePositions.size(); i++) {
            Position position = availablePositions.get(i);
            GlobalObject balloonToSpawn = new GlobalObject(Misc.random(115, 122), position, 0, 10, 60, -1);

            if (i < maxItems) {
                GameItem item = eventItems.remove(Misc.random(eventItems.size() - 1));
                balloonContents.put(position, item);
//                System.out.println("Added item: " + item.getId() + " to position: " + position);
            } else {
                // Add empty balloons to remaining positions
                balloonContents.put(position, null);
//                System.out.println("Added empty balloon at position: " + position);
            }

            Server.getGlobalObjects().add(balloonToSpawn);
            for (Player player : Server.getPlayers()) {
                if (player != null) {
                    player.getPA().object(balloonToSpawn);
                }
            }
        }
    }


    @Override
    public boolean handleObject(Player player, GlobalObject balloon) {
        Event event = EventManager.lastStartedEvent;

        Position balloonPosition = balloon.getPosition();

        if (possibleDropPositions.contains(balloonPosition)) {
            for (Balloon value : Balloon.values()) {
                if (balloon.getObjectId() == value.getObjectId() && balloon.getObjectId() != value.getPoppedObjectId()) {
                    Server.getGlobalObjects().replace(balloon, new GlobalObject(value.getPoppedObjectId(), balloonPosition, 0, 10, 3, -1));

                    // Retrieve the item or null from the balloonContents map using the position
                    GameItem item = balloonContents.get(balloonPosition);
                    player.startAnimation(Misc.random(855,866));
                    if (item != null) {
                        Server.itemHandler.createGroundItem(player, item, balloonPosition, 0);
//                        Server.itemHandler.createUnownedGroundItem(item, balloonPosition);
//                        System.out.println("Player " + player.getName() + " received item: " + item.getId() + " from balloon at position: " + balloonPosition);
                    } else {
                        player.sendMessage("The balloon was empty!");
//                        System.out.println("Player " + player.getName() + " popped an empty balloon at position: " + balloonPosition);
                    }

                    balloonsPopped.add(balloon);

                    if (balloonsPopped.size() >= possibleDropPositions.size()) {
                        endGame();
                    }

                    return true;
                }
            }
        }

        return false;
    }


    private void endGame() {
        PlayerHandler.executeGlobalMessage("<shad=1>[@red@DROP PARTY@bla@] @gre@The Drop Party has concluded!");
        balloonsPopped.forEach(globalObject ->
                Server.getGlobalObjects().replace(globalObject, new GlobalObject(-1, globalObject.getPosition(), 0, 10))
        );
    }

    public enum Balloon {
        YELLOW(115, 123),
        RED(116, 124),
        BLUE(117, 125),
        GREEN(118, 126),
        PURPLE(119, 127),
        WHITE(120, 128),
        LIGHT_BLUE_AND_GREEN_DOUBLE(121, 129),
        RED_YELLOW_BLUE_TRIPLE(122, 130);

        private final int objectId;
        private final int poppedObjectId;

        Balloon(int objectId, int poppedObjectId) {
            this.objectId = objectId;
            this.poppedObjectId = poppedObjectId;
        }

        public int getObjectId() {
            return objectId;
        }

        public int getPoppedObjectId() {
            return poppedObjectId;
        }
    }
}
