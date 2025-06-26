package io.kyros.content.bosses.sarachnis;

import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.Prayer;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.ProjectileBase;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SarachnisRanged implements Function<SarachnisNpc, NPCAutoAttack> {

    private static ProjectileBase projectile() {
        return new ProjectileBaseBuilder()
                .setSendDelay(2)
                .setSpeed(30)
                .setStartHeight(90)
                .setProjectileId(1686)
                .createProjectileBase();
    }

    @Override
    public NPCAutoAttack apply(SarachnisNpc nightmare) {
        Consumer<NPCCombatAttackHit> onHit = t -> {
            if (t.getCombatHit().missed())
                return;
            if (t.getVictim().isPlayer()) {
                Player player = (Player) t.getVictim();
                if (!player.getCombatPrayer().isPrayerActive(Prayer.PROTECT_FROM_MISSILES.getId()) && !player.getCombatPrayer().isPrayerActive(Prayer.DAMPEN_RANGED.getId())) {
                    t.getNpc().appendDamage(10, HitMask.NPC_HEAL);
                    t.getNpc().getHealth().increase(20);
                }
            }
        };
        Consumer<NPCCombatAttack> onAttack = t -> {
                nightmare.attackCounter++;
        };
        List<Player> players = NPCAutoAttack.getPlayers(nightmare);
        return new NPCAutoAttackBuilder()
                .setSelectPlayersForMultiAttack(new Function<>() {
                    @Override
                    public List<Player> apply(NPCCombatAttack npcCombatAttack) {
                        return players.stream().filter(plr -> Boundary.isIn(plr, Boundary.SARACHNIS_LAIR))
                                .collect(Collectors.toList());
                    }
                })
                .setIgnoreProjectileClipping(false)
                .setSelectAutoAttack(attack -> attack.getNpc().distance(attack.getVictim().getPosition()) > 1)
                .setAnimation(new Animation(4410))
                .setCombatType(CombatType.RANGE)
                .setMaxHit(31)
                .setHitDelay(3)
                .setAttackDelay(4)
                .setDistanceRequiredForAttack(24)
                .setMultiAttack(false)
                .setOnHit(onHit)
                .setOnAttack(onAttack)
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.3;
                    }
                })
                .setProjectile(projectile())
                .createNPCAutoAttack();
    }
}