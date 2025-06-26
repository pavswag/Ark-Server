package io.kyros.content.minigames.blastfurnance.dispenser;

import com.google.gson.annotations.Expose;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.minigames.blastfurnance.BlastFurnaceBar;
import io.kyros.content.skills.Skill;
import io.kyros.model.Items;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import io.kyros.util.task.TaskManager;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class BarDispenser {

    @Expose
    private DispenserState state = DispenserState.EMPTY;

    @Expose
    private final Map<BlastFurnaceBar, Integer> bars = new HashMap<>();

    public boolean handleObject(Player player, int objectID) {
        if (objectID == 9092 && state == DispenserState.HOT) {
            if (player.getItems().hasItemOnOrInventory(Items.BUCKET_OF_WATER)) {
                useBucket(player);
            } else {
                player.sendMessage("The dispenser is to hot to touch!");
            }
            return true;
        } else if (objectID == 9092 && state != DispenserState.EMPTY) {
            take(player);
            return true;
        } else if (objectID == 9092) {
            check(player);
            return true;
        }

        return false;
    }

    public void onEnter(Player player) {
        updateVisual(player);
    }

    public void add(BlastFurnaceBar bar, int amount) {
        var newAmount = getAmount(bar) + amount;
        bars.put(bar, newAmount);
    }

    public void remove(BlastFurnaceBar bar, int amount) {
        var newAmount = getAmount(bar) - amount;
        if (newAmount == 0) {
            bars.remove(bar);
            return;
        }
        bars.put(bar, newAmount);
    }

    public void setState(DispenserState state) {
        this.state = state;
    }

    public boolean isFull() {
        return getSpace() == 0;
    }

    public boolean isHot() {
        return state == DispenserState.HOT;
    }

    public boolean isCold() {
        return state == DispenserState.COLD;
    }

    public boolean isMelting() {
        return state == DispenserState.MELTING;
    }

    public boolean isEmpty() {
        return getAmount() == 0;
    }

    public int getSpace() {
        return 1000 - getAmount();
    }

    public int getAmount() {
        var amount = 0;
        for (var type : bars.keySet()) {
            amount += getAmount(type);
        }
        return amount;
    }

    public int getAmount(BlastFurnaceBar bar) {
        return bars.getOrDefault(bar, 0);
    }

    private static void switchState(Player player, DispenserState state) {
        player.getBlastFurnace().getBarDispenser().setState(state);
        updateVisual(player);
    }

    public static void flash() {
        Server.getPlayers().forEach(plr -> {
            if (Boundary.isIn(plr, Boundary.BLAST_FURNACE)) {
                plr.getRegionProvider().get(plr.getX(), plr.getY()).getWorldObject(9092, 1940, 4963, 0).ifPresent(object -> {
                    plr.getPA().sendPlayerObjectAnimation(object.toGlobalObject(), 2440);
                });
            }
        });
    }

    public static void melt(Player player) {
        switchState(player, DispenserState.MELTING);
        TaskManager.submit(2, BarDispenser::flash);
        TaskManager.submit(3, () -> {
            switchState(player, DispenserState.HOT);
            player.sendMessage("@red@Your bar's are ready to be collected!");
        });

        CycleEventHandler.getSingleton().stopEvents(player, CycleEventHandler.Event.BlastFurnaceCoffer);
        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.BlastFurnaceCoffer, player, new CycleEvent() {

            @Override
            public void execute(CycleEventContainer b) {
                if (player == null) {
                    b.stop();
                    return;
                }
                player.getBlastFurnace().getCoffer().withdrawFromCoffer(12);
            }

        }, 1);
    }

    public static void sendInterface(Player player) {
        if (player.getBlastFurnace().getBarDispenser().getAmount() <= 0) {
            return;
        }
        if (player.getBlastFurnace() != null && player.getBlastFurnace().getBarDispenser() != null && player.getBlastFurnace().getBarDispenser().getBars() != null) {
            if (!player.getBlastFurnace().getBarDispenser().getBars().isEmpty()) {
                for (BlastFurnaceBar blastFurnaceBar : player.getBlastFurnace().getBarDispenser().getBars().keySet()) {
                    if (blastFurnaceBar != null) {
                        collect(player, blastFurnaceBar);
                    }
                }
            }
        }
    }

    public static void check(Player player) {
        var state = player.getBlastFurnace().getBarDispenser().getState();
        var text = "";
        switch (state) {
            case EMPTY:
                text = "There is nothing inside the Dispenser.";
                break;
            case HOT:
                text = "The Dispenser is hot.";
                break;
            case MELTING:
                text = "Bars are still being melted.";
                break;
            case COLD:
                text = "The Dispenser is cold.";
                break;
        }
        player.start(new DialogueBuilder(player).statement(text));
    }

    public static void take(Player player) {
        var dispenser = player.getBlastFurnace().getBarDispenser();
        if (dispenser.isEmpty()) {
            player.start(new DialogueBuilder(player).statement("The dispenser doesn't contain any bars."));
            return;
        }

        if (dispenser.isHot() && !isWearingIceGloves(player)) {
            player.start(new DialogueBuilder(player).statement("Too hot!"));
            return;
        }
        if (dispenser.isMelting()) {
            player.start(new DialogueBuilder(player).statement("Your ores are being melted ... please wait!"));
            return;
        }
        if (player.getInventory().freeInventorySlots() == 0) {
            player.start(new DialogueBuilder(player).statement("You don't have any free inventory space."));
            return;
        }

        if (isWearingIceGloves(player) || player.getBlastFurnace().getBarDispenser().isCold()) {
            if (player.getBlastFurnace() == null || player.getBlastFurnace().getBarDispenser() == null || player.getBlastFurnace().getBarDispenser().getBars() == null || player.getBlastFurnace().getBarDispenser().getAmount() <= 0) {
                return;
            }
            player.getBlastFurnace().update();
            player.getBlastFurnace().getBarDispenser().getBars().forEach((blastFurnaceBar, integer) ->  {
                collect(player, blastFurnaceBar);
            });
        } else {
            player.start(new DialogueBuilder(player).statement("The bars are still molten! You need to cool them down."));
        }
    }

    public static void collect(Player player, BlastFurnaceBar bar) {
        var dispenser = player.getBlastFurnace().getBarDispenser();
        final var originalAmount = dispenser.getAmount(bar);
        player.getItems().addItemUnderAnyCircumstance(ItemDef.forId(bar.getBarId()).getNoteId(), originalAmount);
        if (bar == BlastFurnaceBar.GOLD && isWearingGoldsmithGauntlets(player)) {
            player.getPA().addSkillXPMultiplied(56*originalAmount, Skill.SMITHING.getId(), true);
        } else {
            player.getPA().addSkillXPMultiplied((int) bar.getXp() * originalAmount, Skill.SMITHING.getId(), true);
        }
        player.start(new DialogueBuilder(player).statement("You take " + Misc.format(originalAmount) + "x " + bar.getName() + " from the dispenser."));

        if (!player.getAchievements().isComplete(Achievements.Achievement.MASTER_SMITH1)) {
            Achievements.increase(player, AchievementType.SMITH, (originalAmount/3));

        }

        dispenser.getBars().replace(bar, 0);
        if (dispenser.isEmpty()) {
            switchState(player, DispenserState.EMPTY);
        }
    }

    public static void updateVisual(Player player) {
        player.getPA().sendConfig(936, player.getBlastFurnace().getBarDispenser().getState().ordinal());
    }

    public static void useBucket(Player player) {
        if (player.getBlastFurnace().getBarDispenser().isHot()) {
            switchState(player, DispenserState.COLD);
        }
        player.getPA().sendSound(1051);
    }

    public static Optional<GlobalObject> getObject() {
        return Optional.ofNullable(Server.getGlobalObjects().get(9092, 1940, 4963, 0));
    }

    public static boolean isWearingIceGloves(Player player) {
        return player.getItems().hasItemOnOrInventory(Items.ICE_GLOVES);
    }

    public static boolean isWearingGoldsmithGauntlets(Player player) {
        return player.getItems().hasItemOnOrInventory(Items.GOLDSMITH_GAUNTLETS)
                || player.getItems().hasItemOnOrInventory(Items.SMITHING_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.SMITHING_CAPET)
                || player.getItems().hasItemOnOrInventory(Items.MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.ASSEMBLER_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.ARDOUGNE_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.FIRE_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.FIRE_MAX_CAPE_2)
                || player.getItems().hasItemOnOrInventory(Items.GUTHIX_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.INFERNAL_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.SARADOMIN_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.ZAMORAK_MAX_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.MAX_CAPE_2)
                || player.getItems().hasItemOnOrInventory(Items.MYTHICAL_CAPE)
                || player.getItems().hasItemOnOrInventory(Items.MAX_CAPE_3);
    }
}
