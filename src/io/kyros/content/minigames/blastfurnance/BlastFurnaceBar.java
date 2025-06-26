package io.kyros.content.minigames.blastfurnance;

import io.kyros.model.Items;
import io.kyros.model.items.GameItem;
import lombok.Getter;

import java.util.ArrayList;

@Getter
public enum BlastFurnaceBar {

    BRONZE(BlastFurnaceOre.COPPER, BlastFurnaceOre.TIN, Items.BRONZE_BAR, 1, 6.2),
    IRON(BlastFurnaceOre.IRON, Items.IRON_BAR, 15, 12.5),
    SILVER(BlastFurnaceOre.SILVER, Items.SILVER_BAR, 20, 13.6),
    STEEL(BlastFurnaceOre.IRON, BlastFurnaceOre.COAL, Items.STEEL_BAR, 30, 17.5),
    GOLD(BlastFurnaceOre.GOLD, Items.GOLD_BAR, 40, 22.5),
    MITHRIL(BlastFurnaceOre.MITHRIL, BlastFurnaceOre.COAL, 2, Items.MITHRIL_BAR, 50, 30),
    ADAMANTITE(BlastFurnaceOre.ADAMANTITE, BlastFurnaceOre.COAL, 3, Items.ADAMANTITE_BAR, 70, 37.5),
    RUNITE(BlastFurnaceOre.RUNITE, BlastFurnaceOre.COAL, 4, Items.RUNITE_BAR, 85, 50);

    private final BlastFurnaceBarRequirement[] requirements;
    private final int barId;
    private final int levelRequired;
    private final double xp;

    BlastFurnaceBar(BlastFurnaceOre ore, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceBarRequirement[]{BlastFurnaceBarRequirement.create(ore)}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre ore, BlastFurnaceOre ore2, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceOre[]{ore, ore2}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre requirement, BlastFurnaceOre requirement2, int amountNeeded2, int barId, int levelRequired, double xp) {
        this(requirement, BlastFurnaceBarRequirement.create(requirement2, amountNeeded2), barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre requirement, BlastFurnaceBarRequirement requirement2, int barId, int levelRequired, double xp) {
        this(new BlastFurnaceBarRequirement[]{BlastFurnaceBarRequirement.create(requirement), requirement2}, barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceOre[] requirements, int barId, int levelRequired, double xp) {
        this(BlastFurnaceBarRequirement.create(requirements), barId, levelRequired, xp);
    }

    BlastFurnaceBar(BlastFurnaceBarRequirement[] requirements, int barId, int levelRequired, double xp) {
        this.requirements = requirements;
        this.barId = barId;
        this.levelRequired = levelRequired;
        this.xp = xp;
    }

    public GameItem[] getRequirementsAsItems() {
        var items = new ArrayList<GameItem>();
        for (var requirement : requirements) {
            items.add(new GameItem(requirement.getOre().getOreId(), requirement.getAmountRequired()));
        }
        return items.toArray(GameItem[]::new);
    }

    public String getName(){
        return name().toUpperCase();
    }

}

