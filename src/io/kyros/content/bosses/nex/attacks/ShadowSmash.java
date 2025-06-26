package io.kyros.content.bosses.nex.attacks;

import io.kyros.content.combat.HitMask;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.List;

public class ShadowSmash {
    public ShadowSmash(List<Player> targets) {
        ShadowSmash(targets);
    }

    void ShadowSmash(List<Player> targets) {
        for (Player player : targets) {
            if (player != null) {
                player.gfx0(381);
                int dmg = Misc.random(50);
                if (player.protectingMagic()) {
                    dmg = (dmg / 2);
                }

                player.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
            }
        }
    }
}