package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class AccursedSceptre extends Special {

    int pReduction;
    double npcReduction;

    public AccursedSceptre() {
        super(5.0, 1.0, 1.5, new int[] { 27679 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(8532);
        if (player.playerAttackingIndex > 0 && target instanceof Player) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 2339, 43, 31, 37, 10);
        } else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 2339, 43, 31, 37, 10);
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (damage.getAmount() > 0) {
            pReduction = 3;
            npcReduction = .3;
        } else if (damage.getAmount() == 0) {
            pReduction = 20;
            npcReduction = .05;
        }
        if (target instanceof Player) {
            Player playerTarget = ((Player) target);
            if (playerTarget.playerLevel[1] > 0) {
                playerTarget.playerLevel[1] -= ((Player) target).playerLevel[1] / pReduction;
                playerTarget.getPA().refreshSkill(1);
            }
            if (playerTarget.playerLevel[6] > 0) {
                playerTarget.playerLevel[6] -= ((Player) target).playerLevel[1] / pReduction;
                playerTarget.getPA().refreshSkill(6);
            }
        } else {
            NPC npc = ((NPC) target);
            npc.lowerDefence(npcReduction);
        }
    }
}
