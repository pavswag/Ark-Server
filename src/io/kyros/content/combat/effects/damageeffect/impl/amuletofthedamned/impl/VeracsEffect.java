package io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.AmuletOfTheDamnedEffect;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.EquipmentSet;

/**
 * @author Arthur Behesnilian 1:10 PM
 */
public class VeracsEffect implements AmuletOfTheDamnedEffect {

    /**
     * The singleton instance of the Amulet of the damned effect for Veracs
     */
    public static final AmuletOfTheDamnedEffect INSTANCE = new VeracsEffect();

    @Override
    public boolean hasExtraRequirement(Player player) {
        return EquipmentSet.VERAC.isWearingBarrows(player);
    }

    @Override
    public void useEffect(Player player, Entity other, Damage damage) {
        // Effects happen inline elsewhere
    }
}
