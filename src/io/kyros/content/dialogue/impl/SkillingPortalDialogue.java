package io.kyros.content.dialogue.impl;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;

public class SkillingPortalDialogue extends DialogueBuilder {



    public SkillingPortalDialogue(Player player) {
        super(player);
        setNpcId(-1)
                .option(new DialogueOption("Skilling Island", p -> player.getPA().startTeleport(2345,3804, 0, "modern", false)),
                        new DialogueOption("Hunter Area", p -> player.getPA().startTeleport(3560, 4010, 0, "modern", false)),
                        new DialogueOption("Farming", p -> player.getPA().startTeleport(3053, 3301, 0, "modern", false)));

    }
}
