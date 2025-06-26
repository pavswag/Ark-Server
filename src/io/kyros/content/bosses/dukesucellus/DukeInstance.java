package io.kyros.content.bosses.dukesucellus;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.ImmutableItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class DukeInstance extends InstancedArea {

    public static final Boundary DUKE_ZONE = new Boundary(3008, 6400, 3071, 6463); // Define Duke Sucellus' zone boundary

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(false)
            .createInstanceConfiguration();

    @Getter
    @Setter
    private DukeSucellus duke;  // Reference to the Duke Sucellus NPC

    public DukeInstance() {
        super(CONFIGURATION, DUKE_ZONE);
    }


    /**
     * Handle player entry into the Duke Sucellus instance.
     */
    public void enter(Player player) {
        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in an instance.");
            return;
        }
        player.getPA().closeAllWindows();
        player.moveTo(new Position(3039, 6434, getHeight())); // Move player to starting position in the instance
        add(player);

        // Spawn Duke Sucellus (small form) at the starting position
        DukeSucellus dukeNpc = new DukeSucellus(12166, new Position(3036, 6452, getHeight()), this);
        this.duke = dukeNpc;
        add(dukeNpc);

        setupObjects(player);  // Setup environment objects for the instance
        startRedEyeMechanic(this);
    }

    private void setupObjects(Player dukeInstance) {
        GlobalObject[] objects = {
                new GlobalObject(47536, 3034, 6438, dukeInstance.getInstance().getHeight(), 3, 10, -1).setInstance(dukeInstance.getInstance()),  // Vat 1
                new GlobalObject(47536, 3043, 6438, dukeInstance.getInstance().getHeight(), 1, 10, -1).setInstance(dukeInstance.getInstance()),  // Vat 2
                new GlobalObject(47560, 3034, 6434, dukeInstance.getInstance().getHeight(), 1, 10, -1).setInstance(dukeInstance.getInstance()),  // Pestle Mortar
                new GlobalObject(47561, 3044, 6434, dukeInstance.getInstance().getHeight(), 1, 10, -1).setInstance(dukeInstance.getInstance()),  // Pickaxe
                new GlobalObject(47524, 3048, 6453, dukeInstance.getInstance().getHeight(), 0, 10, -1).setInstance(dukeInstance.getInstance()),  // Mushroom 1
                new GlobalObject(47528, 3030, 6453, dukeInstance.getInstance().getHeight(), 0, 10, -1).setInstance(dukeInstance.getInstance()),  // Mushroom 2
                new GlobalObject(47522, 3033, 6448, dukeInstance.getInstance().getHeight(), 2, 10, -1).setInstance(dukeInstance.getInstance()),  // Salt 1
                new GlobalObject(47522, 3043, 6448, dukeInstance.getInstance().getHeight(), 0, 10, -1).setInstance(dukeInstance.getInstance())   // Salt 2
        };
        Arrays.stream(objects).forEach(object -> {
            Server.getGlobalObjects().add(object);
            dukeInstance.getPA().object(object);
        });
    }


    /**
     * Handle wake-up potion interaction to wake Duke Sucellus.
     */
    public void wakeDuke(Player player) {
        if (duke != null && duke.isAsleep()) {
            duke.wakeUp(player);
        }
    }

    /**
     * Handles placing ingredients in the vats and initiates the fermentation process.
     */
    public void placeIngredientsInVats(Player player, GlobalObject vatObject) {
        // Check if player has the required ingredients (12 of each)
        if (player.getItems().getInventoryCount(28346) >= 12 && player.getItems().getInventoryCount(28342) >= 12 && player.getItems().getInventoryCount(28349) >= 12) {
            // Deduct ingredients from the player's inventory
            player.getItems().deleteItem2(28346, 12);  // Arder powder
            player.getItems().deleteItem2(28342, 12);  // Musca powder
            player.getItems().deleteItem2(28349, 12);  // Salax salt

            // Change vat to fermenting state (47536) and start the fermentation process
            vatObject.setInstance(this);
            vatObject.setId(47536);
            Server.getGlobalObjects().add(vatObject);
            player.getPA().object(vatObject);

            player.sendMessage("You place the ingredients in the vats and start the fermentation process.");

            // After a delay, change the vats to ready state (47539)
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    vatObject.setId(47539);  // Change vat to the ready state
                    Server.getGlobalObjects().add(vatObject);
                    player.getPA().object(vatObject);
                    player.sendMessage("The vats have finished fermenting and are ready to collect the potion.");
                    container.stop();
                }
            }, 10);  // Delay for 10 ticks (approx 6 seconds)

        } else {
            player.sendMessage("You need 12 Arder powder, 12 Musca powder, and 12 Salax salt to start the fermentation process.");
        }
    }

    /**
     * Handles collecting the potion when the vats are ready (ID 47539) and changes the vat back to 47536.
     */
    public void collectPotion(Player player, GlobalObject vatObject) {
        if (vatObject.getObjectId() == 47539) {  // Check if the vat is ready
            player.getInventory().addOrDrop(new ImmutableItem(28351, 2));  // Give 2 potions to the player
            player.sendMessage("You collect the finished potions from the vats.");

            // Change the vat back to fermenting state (47536)
            vatObject.setId(47536);
            Server.getGlobalObjects().add(vatObject);
            player.getPA().object(vatObject);
        } else {
            player.sendMessage("The vats are not ready to collect the potion.");
        }
    }

    private void startRedEyeMechanic(DukeInstance dukeInstance) {
        Optional<WorldObject> lowerEyeEast = RegionProvider.getGlobal().get(3049, 6441).getWorldObject(47544, 3049, 6441, getHeight());
        Optional<WorldObject> middleEyeEast = RegionProvider.getGlobal().get(3049, 6445).getWorldObject(47546, 3049, 6445, getHeight());
        Optional<WorldObject> upperEyeEast = RegionProvider.getGlobal().get(3049, 6449).getWorldObject(47545, 3049, 6449, getHeight());

        Optional<WorldObject> lowerEyeWest = RegionProvider.getGlobal().get(3028, 6441).getWorldObject(47545, 3028, 6441, getHeight());
        Optional<WorldObject> middleEyeWest = RegionProvider.getGlobal().get(3028, 6445).getWorldObject(47544, 3028, 6445, getHeight());
        Optional<WorldObject> upperEyeWest = RegionProvider.getGlobal().get(3028, 6449).getWorldObject(47546, 3028, 6449, getHeight());

        AtomicInteger animationIndex = new AtomicInteger(0);
        int[] animationOrder = {1, 2, 3, 2}; // 1: Lower, 2: Middle, 3: Upper

        // Handle eye animations
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (duke == null || !duke.isAsleep()) {
                    container.stop();
                    return;
                }

                int index = animationIndex.getAndIncrement() % animationOrder.length;
                switch (animationOrder[index]) {
                    case 1:
                        animateEye(lowerEyeEast, lowerEyeWest, 6442); // Lower Eye
                        break;
                    case 2:
                        animateEye(middleEyeEast, middleEyeWest, 6446); // Middle Eye
                        break;
                    case 3:
                        animateEye(upperEyeEast, upperEyeWest, 6450); // Upper Eye
                        break;
                }
            }
        }, 3); // Eye animation event every 3 ticks

        // Handle eye damage after animations are triggered
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (duke == null || !duke.isAsleep()) {
                    container.stop();
                    return;
                }

                int idx = animationIndex.get() % animationOrder.length;
                switch (animationOrder[idx]) {
                    case 1:
                        damagePlayersInSpecificZone(6450); // Only damage players in lower zone
                        break;
                    case 2:
                        damagePlayersInSpecificZone(6446); // Only damage players in middle zone
                        break;
                    case 3:
                        damagePlayersInSpecificZone(6442); // Only damage players in upper zone
                        break;
                }
            }
        }, 1); // Eye damage event triggered every 1 tick after the animation
    }

    /**
     * Animate the eye objects within the instance.
     * @param eastEye The east eye object
     * @param westEye The west eye object
     * @param yCoord The Y-coordinate for this eye's animation
     */
    private void animateEye(Optional<WorldObject> eastEye, Optional<WorldObject> westEye, int yCoord) {
        eastEye.ifPresent(east -> getPlayers().forEach(player -> player.getPA().sendPlayerObjectAnimation(east.toGlobalObject(), 10187)));
        westEye.ifPresent(west -> getPlayers().forEach(player -> player.getPA().sendPlayerObjectAnimation(west.toGlobalObject(), 10188)));
    }

    /**
     * Damage players who are standing in the specific danger zone for the specified Y-coordinate.
     * This ensures that only players in the correct Y-coordinate zone for the active eye are damaged.
     * @param activeY The Y-coordinate of the currently active eye's damage zone
     */
    private void damagePlayersInSpecificZone(int activeY) {
        getPlayers().forEach(player -> {
            if (player.getPosition().getY() == activeY) { // Only apply damage to players in the correct Y-coordinate zone
                if ((player.getX() >= 3029 && player.getX() <= 3032) || (player.getX() >= 3046 && player.getX() <= 3049)) {
                    player.appendDamage(Misc.random(40, 69), HitMask.HIT);
                }
            }
        });
    }


    @Override
    public void onDispose() {
        System.out.println("Disposed of the Duke Sucellus instance");
    }
}
