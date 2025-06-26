package io.kyros.content.bosses.nightmare.attack;

import io.kyros.content.bosses.nightmare.Nightmare;
import io.kyros.content.bosses.nightmare.NightmareAttack;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;

public class Curse extends NightmareAttack {

    @Override
    public void tick(Nightmare nightmare) {
        if (getTicks() == 0) {
            nightmare.requestTransform(9426);
            nightmare.startAnimation(8599);
        }

        if (getTicks() == 2) {
            nightmare.getInstance().getPlayers().forEach(player -> {
                player.getCombatPrayer().shiftProtectionPrayersRight(true);
                player.getPA().sendScreenFlash(0xE73158, 80);
                player.sendMessage("@pur@The Nightmare has cursed you, shuffling your prayers!");
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (container.getTotalTicks() == 50 || !nightmare.checkPlayerState(player)) {
                            container.stop();
                        }
                    }

                    @Override
                    public void onStopped() {
                        player.getCombatPrayer().shiftProtectionPrayersRight(false);
                        player.sendMessage("@gre@You feel the effects of the Nightmare's curse wear off.");
                    }
                }, 1);
            });
        }

        if (getTicks() == 3) {
            nightmare.transformToStandard();
            stop();
        }
    }

}
