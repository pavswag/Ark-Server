package io.kyros.content.bosses.nex.attacks;

import io.kyros.content.combat.HitMask;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.List;

public class Wrath {
    public Wrath(NPC npc, List<Player> targets) {
        Wrath(npc, targets);
    }

    void Wrath(NPC npc, List<Player> targets) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalTicks() > 5) {
                    for (Player player : targets) {
                        if(player.getPosition().getAbsDistance(npc.getPosition()) < 5) {
                            player.appendDamage(Misc.random(10, 40), HitMask.HIT);
                        }
                    }
                    container.stop();
                }
            }
        }, 1);
    }


}
