package io.kyros.content.bosses.nightmare.attack;

import java.util.function.Consumer;
import java.util.function.Function;

import io.kyros.content.bosses.nightmare.Nightmare;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.ProjectileBase;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class StandardMage implements Function<Nightmare, NPCAutoAttack> {

    // In the video I saw random projectiles
    // he had other players hidden
    public static Consumer<NPCCombatAttack> getProjectileThrow(ProjectileBase projectileBase) {
        return new Consumer<NPCCombatAttack>() {
            @Override
            public void accept(NPCCombatAttack npcCombatAttack) {
                int it = 0;
                for (Position border : npcCombatAttack.getNpc().getBorder()) {
                    if (++it % 2 == 0) {
                        Position delta = border.delta(npcCombatAttack.getNpc().getCenterPosition());
                        projectileBase.createTargetedProjectile(npcCombatAttack.getNpc(),
                                border.translate(-delta.getX() + Misc.trueRand(3), -delta.getY() + Misc.trueRand(3)))
                                .send(npcCombatAttack.getNpc().getInstance());
                    }
                }
            }
        };
    }

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(4)
                .setSpeed(15)
                .setStartHeight(90)
                .setProjectileId(1764)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(Nightmare nightmare) {
        return new NPCAutoAttackBuilder()
                .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                .setProjectile(projectile())
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.3;
                    }
                })
                //.setOnAttack(getProjectileThrow(projectile()))
                .setEndGraphic(new Graphic(1765, Graphic.GraphicHeight.HIGH))
                .setAnimation(new Animation(8595))
                .setCombatType(CombatType.MAGE)
                .setMaxHit(30)
                .setHitDelay(4)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(16)
                .setMultiAttack(true)
                .createNPCAutoAttack();
    }
}
