package io.kyros.model.entity.npc.autoattacks;

import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;

public class MeleeSwordSwing extends NPCAutoAttackBuilder {

    public MeleeSwordSwing(int maxHit) {
        setAttackDelay(4);
        setMaxHit(maxHit);
        setAnimation(new Animation(451));
        setCombatType(CombatType.MELEE);
    }
}
