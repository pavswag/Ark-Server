package io.kyros.content.bosses.nex.attacks;

import io.kyros.content.combat.HitMask;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.List;

public class BloodBarrage {


    public BloodBarrage(NPC npc, Player player, List<Player> players) {
        BloodBarrage(npc, player, players);
    }

    void BloodBarrage(NPC npc, Player player, List<Player> players) {

        for (Player possibleTargets: players) {
            if(player.getPosition().getAbsDistance(possibleTargets.getPosition()) <= 3) {
                player.getPA().createPlayersProjectile(npc.getX(), npc.getY(), player.getX(), player.getY(), 16, 10, 374, 43, 43, -1, 65, 3);
                possibleTargets.gfx100(377);
                int dam;
                if(possibleTargets.protectingMagic())
                    dam = Misc.random(40);
                else
                    dam = Misc.random(60);
                if (npc.getHealth().getCurrentHealth() >= 99) {
                    npc.appendHeal(dam, HitMask.NPC_HEAL);
                }
                if (possibleTargets.usingInfAgro) {
                    possibleTargets.attackEntity(npc);
                }
                possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
            }

        }
    }
}
