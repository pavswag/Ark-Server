package io.kyros.content.skills.farming;

import io.kyros.Server;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class FarmingTeleport extends DialogueBuilder {

    public static final int NPC = 3247;

    private final Player player;
    private final NPC npc;

    public FarmingTeleport(Player player, NPC npc) {
        super(player);
        this.player = player;
        this.npc = npc;
        option(new DialogueOption("Falador", p -> tele(new Position(3053, 3301), false)),
                new DialogueOption("Catherby", p -> tele(new Position(2815, 3462), false)),
                new DialogueOption("Ardougne", p -> tele(new Position(2663, 3375), false)),
                new DialogueOption("Port Phasmatys", p -> tele(new Position(3603, 3529), false))
        );
    }

    private void tele(Position position, boolean skip) {
        if (!skip && (Misc.trueRand(50_000) == 0 || Server.isDebug() && Misc.trueRand(3) == 0)) {
            player.start(new DialogueBuilder(player).setNpcId(NPC).npc("Teleport man go *brrrr*").exit(p -> tele(position, true)));
        } else {
            player.getPA().startTeleport(position, "modern", false);
        }
    }
}
