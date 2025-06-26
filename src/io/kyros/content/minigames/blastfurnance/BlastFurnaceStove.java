package io.kyros.content.minigames.blastfurnance;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.skills.Skill;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import lombok.Getter;

import static io.kyros.content.minigames.blastfurnance.conveyor.BlastFurnaceState.*;

@Getter
public class BlastFurnaceStove {

    private int temperature;
    private int coal;

    private static final Position LOCATION = new Position(1948, 4963);

    public BlastFurnaceStove() {
        temperature = 50;
        coal = 100;
    }

    public void update() {
        BlastFurnace.getDumpy().addCoke();
        removeCoal(1);
        temperature = coal / 2;
        if (temperature < 10) {
            if (BlastFurnace.isRunning()) {
                BlastFurnace.switchState(BROKEN);
            }
        }
    }

    public void refuel(Player player) {
        if(player.getItems().getInventoryCount(6448) <= 0) {
            player.start(new DialogueBuilder(player).statement("You need a spade full of coke to refuel the stove."));
            return;
        }
        player.startAnimation(2442);
        player.getPA().addSkillXPMultiplied(player.playerLevel[Skill.FIREMAKING.getId()] * 10, 11, true);
        player.getPA().sendSound(1059);
        player.getItems().deleteItem(6448, 1);
        player.getItems().addItem(Items.SPADE, 1);
        addCoal(10);
    }

    public void collect(Player player) {
        if (!player.getItems().hasItemOnOrInventory(Items.SPADE)) {
               player.start(new DialogueBuilder(player).statement("You will need a spade to collect coke."));
                return;
        }
        if (player.getItems().getInventoryCount(6448) >= 1) {
                player.start(new DialogueBuilder(player).statement("Your spade is already full of coke."));
                return;
        }
        player.startAnimation(2441);
        player.getPA().sendSound(1049);
        player.getItems().addItem(6448, 1);
        player.getItems().deleteItem(Items.SPADE, 1);
    }

    public boolean canRun() {
        return temperature > 10;
    }

    public void addCoal(int amount) {
        if (coal < 200) {
            coal += amount;
        }
        if (coal > 200) {
            coal = 200;
        }
    }

    public void removeCoal(int amount) {
        coal -= amount;
        if (coal < 0) {
            coal = 0;
        }
    }

}

