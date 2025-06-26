package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.NpcOverrides;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class Lostsoul extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        /**
         * oratio [-25790, -25801, -24787, -24816]
         * sanitas [-31934, -31945, -31955, -31984]
         * mors [26434, 26423, 26413, 26386]
         */
        for(int i = 0; i < 3; i++) {
            short[] colors = null;
            switch (i) {
                case 0:
                    colors = new short[] { -25790, -25801, -24787, -24816 };
                    break;
                case 1:
                    colors = new short[] { -31934, -31945, -31955, -31984 };
                    break;
                case 2:
                    colors = new short[] { 26434, 26423, 26413, 26386 };
                    break;
            }
            NPC npc = new NPC(12212, player.getPosition().deepCopy().translate(Misc.random(3), Misc.random(3)));
            npc.setModelOverride(new NpcOverrides(null, colors, null, false));
            NPCSpawning.spawn(npc);
        }
    }
}
