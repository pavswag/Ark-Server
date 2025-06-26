package io.kyros.content.bosses.nex.attacks;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.List;

public class Containment {
    public Containment(NPC npc, List<Player> targets) {
        Containment(npc, targets);
    }

    void Containment(NPC npc, List<Player> targets) {
        Boundary iceBounds = new Boundary(npc.getX() - 2, npc.absY - 3, npc.getX() + 3, npc.getY() + 2);
        for(int y = -3; y < 4; y++) {
            GlobalObject obj1 = new GlobalObject(42943, npc.absX + -3, npc.absY + y, npc.getHeight(), 0, 10, 6);
            GlobalObject obj2 = new GlobalObject(42943, npc.absX + 3, npc.absY + y, npc.getHeight(), 0, 10, 6);
            GlobalObject obj3 = new GlobalObject(42943, npc.absX + y, npc.absY + -3, npc.getHeight(), 0, 10, 6);
            GlobalObject obj4 = new GlobalObject(42943, npc.absX + y, npc.absY + 3, npc.getHeight(), 0, 10, 6);
            Server.getGlobalObjects().add(obj1);
            Server.getGlobalObjects().add(obj2);
            Server.getGlobalObjects().add(obj3);
            Server.getGlobalObjects().add(obj4);

        }


        for (Player player: targets) {
            if(iceBounds.in(player)) {
                player.appendDamage(Misc.random(20, 60), HitMask.HIT);
            }
        }
    }
}