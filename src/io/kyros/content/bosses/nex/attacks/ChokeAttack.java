package io.kyros.content.bosses.nex.attacks;

import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.concurrent.TimeUnit;

public class ChokeAttack {
    public ChokeAttack(Player target) {
        ChokeAttack(target);
    }

    void ChokeAttack(Player target) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!Boundary.isIn(target, Boundary.NEX)) {
                    target.nexVirusTimer = 0;
                    target.hasNexVirus = false;
                    target.hasHadNexVirus = false;
                    container.stop();
                }
                if (target.hasNexVirus && target.nexVirusTimer <= 0) {
                    target.hasNexVirus = false;
                    target.hasHadNexVirus = false;
                    container.stop();
                }
                if (!target.hasNexVirus) {
                    target.forcedChat("*Cough*");
                    target.nexVirusTimer = 100;
                    target.hasNexVirus = true;
                    target.hasHadNexVirus = true;
                    target.TimeSinceVirus = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10));
                }
                if (target.nexVirusTimer > 0) {
                    target.forcedChat("*Cough*");
                }
                target.nexVirusTimer -= 15;

/*                for (Player player : Server.getPlayers().toPlayerArray()) {
                    if (player != target && !player.hasNexVirus && player.getPosition().withinDistance(target.getPosition(), 2)) {
                        if (!player.hasHadNexVirus && player.TimeSinceVirus <= System.currentTimeMillis()) {
                            new ChokeAttack(player);
                        }
                    }
                }*/
            }
        }, 15);
    }
}
