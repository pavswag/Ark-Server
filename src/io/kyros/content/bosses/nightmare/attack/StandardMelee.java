package io.kyros.content.bosses.nightmare.attack;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.kyros.content.bosses.nightmare.Nightmare;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.player.Player;

public class StandardMelee implements Function<Nightmare, NPCAutoAttack> {

    @Override
    public NPCAutoAttack apply(Nightmare nightmare) {
        return new NPCAutoAttackBuilder()
                .setAnimation(new Animation(8594))
                .setCombatType(CombatType.MELEE)
                .setMaxHit(35)
                .setHitDelay(2)
                .setAttackDelay(6)
                .setDistanceRequiredForAttack(1)
                .setSelectAutoAttack(new Function<NPCCombatAttack, Boolean>() {
                    @Override
                    public Boolean apply(NPCCombatAttack npcCombatAttack) {
                        return npcCombatAttack.getNpc().distance(npcCombatAttack.getVictim().getPosition()) <= 1;
                    }
                })
                .setMultiAttack(true)
                .setSelectPlayersForMultiAttack(new Function<>() {
                    @Override
                    public List<Player> apply(NPCCombatAttack npcCombatAttack) {
                        return npcCombatAttack.getNpc().getInstance().getPlayers().stream().filter(plr -> plr.distance(npcCombatAttack.getVictim().getPosition()) <= 3)
                                .collect(Collectors.toList());
                    }
                })
                .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        return 0.2d;
                    }
                })
                .setMaxHitBonus(new Function<NPCCombatAttack, Double>() {
                    @Override
                    public Double apply(NPCCombatAttack npcCombatAttack) {
                        if (npcCombatAttack.getVictim().isPlayer()) {
                            Player player = npcCombatAttack.getVictim().asPlayer();
                            if (!player.protectingMelee()) {
                                return 0.2d;
                            }
                        }
                        return 0d;
                    }
                })
                .createNPCAutoAttack();
    }
}
