package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.SoundType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class SoulReaper extends Special {

    public SoulReaper() {
        super(10.0, 1.5, 1.5, new int[] { 28338 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(10173);
        player.gfx0(2430);
        player.getPA().sendSound(3869, SoundType.AREA_SOUND);
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (damage.getAmount() > 0) {
            if (target instanceof Player) {
                Player playerTarget = ((Player) target);
                int[] skillOrder = { 1, 2, 5, 0, 6, 4};

                int totalDamage = damage.getAmount();

                for (int i : skillOrder) {
                    if (totalDamage <= 0)
                        break;

                    int currentLevel = playerTarget.playerLevel[i];
                    if (currentLevel > 0) {
                        int drainAmount = Math.min(totalDamage, currentLevel);

                        playerTarget.playerLevel[i] -= drainAmount;
                        playerTarget.getPA().refreshSkill(i);

                        totalDamage -= drainAmount;
                    }
                }

            } else {
                NPC npc = ((NPC) target);
                if (player.debugMessage) {
                    player.sendMessage("BGS, npc defence before: " + npc.getDefence());
                }
                if (npc.getNpcId() == 11775) {
                    npc.lowerDefence(5.0);
                } else {
                    npc.lowerDefence(0.3);
                }
                if (player.debugMessage) {
                    player.sendMessage("BGS, npc defence after: " + npc.getDefence());
                }
            }
        }
    }
}
