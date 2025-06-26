package io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.AmuletOfTheDamnedEffect;
import io.kyros.content.items.Degrade;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.EquipmentSet;
import io.kyros.util.Misc;

/**
 * @author Arthur Behesnilian 1:46 PM
 */
public class GuthanEffect implements AmuletOfTheDamnedEffect {

    /**
     * The singleton instance of the Amulet of the damned effect for Guthan
     */
    public static final AmuletOfTheDamnedEffect INSTANCE = new GuthanEffect();

    @Override
    public boolean hasExtraRequirement(Player player) {
        return EquipmentSet.GUTHAN.isWearingBarrows(player);
    }

    @Override
    public void useEffect(Player player, Entity other, Damage damage) {
        int maximumHealthCap = player.getHealth().getMaximumHealth() + 10;

        if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33117) && Misc.random(1, 20) <= 3 && player.wildLevel < 0) {
            damage.setAmount(damage.getAmount()*2);
        }

        player.getHealth().increase(damage.getAmount(), maximumHealthCap);
        Degrade.degrade(player, Degrade.DegradableItem.AMULETS_OF_THE_DAMNED);
    }
}
