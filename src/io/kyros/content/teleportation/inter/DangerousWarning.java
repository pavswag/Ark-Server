package io.kyros.content.teleportation.inter;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class DangerousWarning {

    public static void show(Player player, Position pos) {
        if (!player.getPosition().inWild()) {
            player.start(new DialogueBuilder(player).statement("@red@This teleport is DANGEROUS, are you sure?")
                    .option(
                            new DialogueOption("Yes, teleport to the dangerous area.", plr -> handleTeleport(plr, pos, false)),
                            new DialogueOption("No, stay here.", plr -> player.getPA().closeAllWindows())
                    )
            );
        } else {
            handleTeleport(player, pos, false);
        }
    }

    public static void handleTeleport(Player player, Position position, boolean warning) {
        if (warning) {
            show(player, position);
            return;
        }

        player.getPA().closeAllWindows();
        player.getAttributes().remove("dangerous_tele");
        player.getPA().startTeleport(position.getX(), position.getY(), position.getHeight(), "modern", false);
    }
}
