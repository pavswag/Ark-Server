package io.kyros.content.minigames.blastfurnance.conveyor;

import com.google.gson.annotations.Expose;
import io.kyros.content.minigames.blastfurnance.BlastFurnaceOre;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ConveyorBelt {

    @Expose
    private final Map<BlastFurnaceOre, Integer> content = new HashMap<>();

    public void add(BlastFurnaceOre ore, int amount) {
        var newAmount = getAmount(ore) + amount;
        content.put(ore, newAmount);
    }

    public void remove(BlastFurnaceOre ore, int amount) {
        var newAmount = getAmount(ore) - amount;
        if (newAmount == 0) {
            content.remove(ore);
            return;
        }
        content.put(ore, newAmount);
    }

    public int getSpace(BlastFurnaceOre ore) {
        return ore.getCapacity() - getAmount(ore);
    }

    public int getAmount() {
        var amount = 0;
        for (var type : content.keySet()) {
            amount += getAmount(type);
        }
        return amount;
    }

    public int getAmount(BlastFurnaceOre ore) {
        return content.getOrDefault(ore, 0);
    }

}

