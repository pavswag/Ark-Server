package io.kyros.model.entity.npc.autoattacks;

import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBaseBuilder;

public class RangedShootArrow extends NPCAutoAttackBuilder {

    public RangedShootArrow(int maxHit) {
        setAttackDelay(3);
        setMaxHit(maxHit);
        setAnimation(new Animation(426));
        setCombatType(CombatType.RANGE);
        setDistanceRequiredForAttack(10);
        setHitDelay(3);
        setStartGraphic(new Graphic(19, Graphic.GraphicHeight.MIDDLE));
        setProjectile(new ProjectileBaseBuilder().setSendDelay(2).setProjectileId(11).createProjectileBase());
    }
}
