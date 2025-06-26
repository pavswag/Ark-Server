package io.kyros.content.minigames.blastfurnance;

import com.google.gson.annotations.Expose;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.minigames.blastfurnance.conveyor.BlastFurnaceState;
import io.kyros.content.minigames.blastfurnance.conveyor.ConveyorBelt;
import io.kyros.content.minigames.blastfurnance.dispenser.BarDispenser;
import io.kyros.content.minigames.blastfurnance.npcs.BlastFurnaceOreNpc;
import io.kyros.content.minigames.blastfurnance.npcs.Dumpy;
import io.kyros.content.skills.Skill;
import io.kyros.model.Items;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import io.kyros.util.task.TaskManager;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.kyros.content.minigames.blastfurnance.conveyor.BlastFurnaceState.BROKEN;
import static io.kyros.content.minigames.blastfurnance.conveyor.BlastFurnaceState.RUNNING;

@Getter
@SuppressWarnings("unused")
public class BlastFurnace {

    private static final Map<Player, Set<BlastFurnaceOreNpc>> NPCS = new HashMap<>();
    private static final BlastFurnaceStove STOVE = new BlastFurnaceStove();
    private static BlastFurnaceState state = RUNNING;
    public static final int COST = 2_520_000;

    private Player player;

    @Expose
    private final Map<BlastFurnaceOre, Integer> inMachine;
    @Expose
    private long lastUsed;
    @Getter
    @Expose
    private final ConveyorBelt conveyorBelt;
    @Getter
    @Expose
    private final BarDispenser barDispenser;

    @Getter
    @Expose
    private final BlastFurnaceCoffer coffer ;


    public BlastFurnace() {
        this.inMachine = new HashMap<>();
        this.coffer = new BlastFurnaceCoffer();
        this.conveyorBelt = new ConveyorBelt();
        this.barDispenser = new BarDispenser();
    }

    static {
        startTask();
    }

    public boolean handleObjects(Player player, int objectID) {
        if (objectID == 29330) {
            BlastFurnaceCoffer.use(player);
            return true;
        }
        if (objectID == 9098) {
            checkPot(player);
            return true;
        }
        if (objectID == 9100) {
            addToMachine(player);
            return true;
        }
        if (objectID == 9143) {
            if (player.getItems().getInventoryCount(Items.BUCKET) <= 0) {
                player.sendMessage("You don't have anything to fill with water.");
                return true;
            }
            player.getItems().deleteItem(Items.BUCKET, 1);
            player.getItems().addItem(Items.BUCKET_OF_WATER, 1);
            player.sendMessage("You fill your bucket with water.");
            player.getPA().sendSound(2609);
            return true;
        }
        if (objectID == 9088) {
            STOVE.collect(player);
            return true;
        }
        if (objectID == 9085) {
            STOVE.refuel(player);
            return true;
        }
        if (objectID == 9089) {
            player.start(new DialogueBuilder(player).statement("The gauage shows the temperature is : " + STOVE.getTemperature()));
        return true;
        }
        if (objectID == 9090) {
            player.getPA().forceMove(1950,4961,0,false);

            if (CycleEventHandler.getSingleton().isAlive(player)) {
                CycleEventHandler.getSingleton().stopEvents(player);
            }
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if (!player.getPosition().equals(new Position(1950,4961))) {
                        player.stopAnimation();
                        container.stop();
                        return;
                    }
                    if (Misc.random(300) == 0 && player.getInterfaceEvent().isExecutable()) {
                        player.stopAnimation();
                        player.getInterfaceEvent().execute();
                        container.stop();
                        return;
                    }
                    player.facePosition(1949,4961);
                    player.startAnimation(2432);
                    player.getPA().addSkillXPMultiplied(2, Skill.STRENGTH.getId(), true);
                }
            }, 3);

            return true;
        }
        if (objectID == 9138) {
            player.sendMessage("Meh stairs...");
            return true;
        }

        return false;
    }

    public void update() {
        lastUsed = System.currentTimeMillis();
    }

    public void sendToBelt(BlastFurnaceOre ore) {
        getConveyorBelt().add(ore, getAmountInMachine(ore));
        inMachine.remove(ore);
        sendToDispenser();
    }

    public void sendToDispenser() {
        for (var barIndex = BlastFurnaceBar.values().length - 1; barIndex >= 0; barIndex--) {
            var bar = BlastFurnaceBar.values()[barIndex];
            while (hasRequirements(bar) && !getBarDispenser().isFull()) {
                for (var requirement : bar.getRequirements()) {
                    getConveyorBelt().remove(requirement.getOre(), requirement.getAmountRequired());
                }
                getBarDispenser().add(bar, 1);
            }
        }
    }

    public boolean isWorking() {
        return getLastUsed() <= 60;
    }

    public boolean canUse() {
        return coffer.getCoinsInCoffer() > 12 && isRunning();
    }

    public int getLastUsed() {
        return (int) (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastUsed));
    }

    public Map<BlastFurnaceOre, Integer> getOresInMachine() {
        return inMachine;
    }

    public int getAmountInMachine(BlastFurnaceOre ore) {
        return inMachine.getOrDefault(ore, 0);
    }

    public int getAmountInMachine() {
        var amount = 0;
        for (var ore : inMachine.values()) {
            amount += ore;
        }
        return amount;
    }

    public boolean hasRequirements(BlastFurnaceBar bar) {
        for (var requirement : bar.getRequirements()) {
            if (getConveyorBelt().getAmount(requirement.getOre()) < requirement.getAmountRequired()) {
                return false;
            }
        }
        return true;
    }

    public static void process() {
        if (Boundary.getPlayersInBoundary(Boundary.BLAST_FURNACE) <= 0) {
            return;
        }
        updateConveyorBelt();
        updateDriveBelt();
        updateGearBox();
        STOVE.update();
    }

    public static void addToMachine(Player player) {
        if (player.getBlastFurnace().getBarDispenser().isFull()) {
            player.start(new DialogueBuilder(player).statement("You should collect your bars before making any more."));
            return;
        }
        if (player.getBlastFurnace().getCoffer().getCoinsInCoffer() <= 0) {
            player.start(new DialogueBuilder(player).statement("You don't have any coins in your coffer yet."));
            return;
        }
        /*if (player.getBlastFurnace().getAmountInMachine() > 0) {
            //very very very slim chance of this occurring
            player.sendMessage("Please wait for ores to process before adding more...");
            return;
        }*/
        var oresInInventory = getOresInInventory(player);
        if (oresInInventory.isEmpty()) {
            player.start(new DialogueBuilder(player).statement("You don't have anything suitable for putting into the blase furnace."));
            return;
        }

        var noSpace = false;
        for (var ore : oresInInventory.keySet()) {

            for (BlastFurnaceOre blastFurnaceOre : player.getBlastFurnace().getInMachine().keySet()) {
                if (blastFurnaceOre.getOreId() != ore.getOreId() && player.getBlastFurnace().getInMachine().get(blastFurnaceOre) < 0 && ore.getOreId() != 453) {
                    player.sendMessage("You cannot add more than one type of ore other than coal at a time!");
                    return;
                }
            }

            var amount = getAmountInInventory(player, ore);
            var beltSpace = player.getBlastFurnace().getConveyorBelt().getSpace(ore);

            if (beltSpace <= 0) {
                noSpace = true;
                continue;
            }
            if (amount > beltSpace) {
                amount = beltSpace;
            }
            player.getItems().deleteItem2(ore.getOreId(), amount);
            player.getBlastFurnace().getOresInMachine().put(ore, amount);
            player.sendMessage("All your ores goes onto the conveyor belt.");
            addNpc(player, ore);
            player.getBlastFurnace().update();
        }
        if (noSpace) {
            player.sendMessage("Your belt is getting full...");
        }
    }

    public static void startTask() {
        TaskManager.submit(Misc.random(100, 1000), () -> switchState(BROKEN));
    }

    public static void repair() {
        TaskManager.submit(10, () -> switchState(RUNNING));
    }

    public static void onEnter(Player player) {
        if (player.getBlastFurnace().getConveyorBelt().getAmount() > 0) {
            for (var ore : player.getBlastFurnace().getOresInMachine().keySet()) {
                addNpc(player, ore);
            }
        }
        player.getBlastFurnace().getBarDispenser().onEnter(player);
    }


    public static void checkPot(Player player) {
        var bf = player.getBlastFurnace().getConveyorBelt();
        player.start(new DialogueBuilder(player).statement("Coal: " + bf.getAmount(BlastFurnaceOre.COAL),
                "Tin Ore: " + bf.getAmount(BlastFurnaceOre.TIN),
                "Copper Ore: " + bf.getAmount(BlastFurnaceOre.COPPER),
                "Iron Ore: " + bf.getAmount(BlastFurnaceOre.IRON)).next(new DialogueBuilder(player).statement("Silver Ore: " + bf.getAmount(BlastFurnaceOre.SILVER),
                "Gold Ore: " + bf.getAmount(BlastFurnaceOre.GOLD),
                "Mithril Ore: " + bf.getAmount(BlastFurnaceOre.MITHRIL),
                "Adamantite Ore: " + bf.getAmount(BlastFurnaceOre.ADAMANTITE)).next(new DialogueBuilder(player).statement("Runite Ore: " + bf.getAmount(BlastFurnaceOre.RUNITE)))));
    }

    public static void switchState(BlastFurnaceState state) {
        if (Boundary.getPlayersInBoundary(Boundary.BLAST_FURNACE) <= 0) {
            return;
        }
        if (state == BROKEN) {
            BlastFurnace.state = state;
            repair();
        } else {
            if (getStove().canRun()) {
                BlastFurnace.state = state;
                startTask();
                startOreNpcs();
            }
        }
    }

    public static BlastFurnaceOre getMostAmount(Player player) {
        BlastFurnaceOre result = BlastFurnaceOre.COPPER;
        var amount = 0;
        for (var ore : BlastFurnaceOre.values()) {
            if (player.getItems().getInventoryCount(ore.getOreId()) > amount) {
                result = ore;
            }
        }
        return result;
    }

    public static Map<BlastFurnaceOre, Integer> getOresInInventory(Player player) {
        var map = new HashMap<BlastFurnaceOre, Integer>();
        for (var ore : BlastFurnaceOre.values()) {
            var amount = getAmountInInventory(player, ore);
            if (amount > 0) {
                map.put(ore, amount);
            }
        }
        return map;
    }

    public static int getAmountInInventory(Player player, BlastFurnaceOre ore) {
        return player.getItems().getInventoryCount(ore.getOreId());
    }

    public static boolean isRunning() {
        return state == RUNNING;
    }

    public static boolean isBroken() {
        return state == BROKEN;
    }

    public static Dumpy dumpy = null;
    public static Dumpy getDumpy() {
        if (dumpy != null) {
            return dumpy;
        }
        return dumpy = new Dumpy(7386, new Position(1950, 4963,0));
    }

    public static BlastFurnaceStove getStove() {
        return STOVE;
    }

    public static void updateGearBox() {
        Server.getPlayers().forEach(plr -> {
            if (Boundary.isIn(plr, Boundary.BLAST_FURNACE)) {
                if (isRunning()) {
                    plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9106, 1945, 4966, 0).ifPresent(object-> plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), 2436));
                }
            }
        });
    }

    public static void updateConveyorBelt() {
        if (isBroken()) {
            return;
        }
        Server.getPlayers().forEach(plr -> {
            if (Boundary.isIn(plr, Boundary.BLAST_FURNACE)) {
                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9100, 1943, 4967, 0).ifPresent(object-> plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), 2437));
                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9101, 1943, 4966, 0).ifPresent(object-> plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), 2437));
                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9101, 1943, 4965, 0).ifPresent(object-> plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), 2437));
            }
        });
    }

    private static GlobalObject driveBelt1, driveBelt2, cog1, cog2; //cache the objects!

    public static void updateDriveBelt() {
        if (isBroken()) {
            return;
        }
        Server.getPlayers().forEach(plr -> {
            if (Boundary.isIn(plr, Boundary.BLAST_FURNACE)) {
                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9102, 1944, 4967, 0).ifPresent(object -> {
                    var animationId = 2438;
                    if (isBroken()) {
                        animationId = 2451;
                    }
                    plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), animationId);
                });

                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9107, 1944, 4965, 0).ifPresent(object -> {
                    var animationId = 2438;
                    if (isBroken()) {
                        animationId = 2451;
                    }
                    plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), animationId);
                });

                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9104, 1945, 4967, 0).ifPresent(object -> {
                    var animationId = 2438;
                    if (isBroken()) {
                        animationId = 2451;
                    }
                    plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), animationId);
                });

                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9108, 1945, 4965, 0).ifPresent(object -> {
                    var animationId = 2438;
                    if (isBroken()) {
                        animationId = 2451;
                    }
                    plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), animationId);
                });
            }

        });
    }

    public static void removeNpc(Player player, BlastFurnaceOre ore) {
        getOreNPC(player, ore).ifPresent(npc -> {
            npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
            npc.unregister();
        });
        NPCS.get(player).removeIf(npc -> npc.getOre() == ore);
    }

    public static void removeAllNpcs(Player player) {
        NPCS.remove(player);
    }

    public static void onLeave(Player player) {
        player.getPA().walkableInterface(-1);
        if (NPCS.containsKey(player)) {
            removeAllNpcs(player);
        }
    }

    public static void addNpc(Player player, BlastFurnaceOre ore) {
        NPCS.putIfAbsent(player, new HashSet<>());
        NPCS.get(player).add(BlastFurnaceOreNpc.create(ore, player));
        if (player.getBlastFurnace().canUse()) {
            getOreNPC(player, ore).ifPresent(BlastFurnaceOreNpc::start);
        }
    }

    private static void startOreNpcs() {
        Server.getPlayers().forEach(plr -> {
            if (Boundary.isIn(plr, Boundary.BLAST_FURNACE)) {
                startOreNpcs(plr);
            }
        });
    }

    public static void startOreNpcs(Player player) {
        if (player.getBlastFurnace().getConveyorBelt().getAmount() > 0 && player.getBlastFurnace().canUse()) {
            for (var ore : player.getBlastFurnace().getOresInMachine().keySet()) {
                getOreNPC(player, ore).ifPresent(BlastFurnaceOreNpc::start);
            }
        }
    }

    public static Optional<BlastFurnaceOreNpc> getOreNPC(Player player, BlastFurnaceOre ore) {
        for (var npc : NPCS.get(player)) {
            if (npc.getOre() == ore) {
                return Optional.of(npc);
            }
        }
        return Optional.empty();
    }
}

