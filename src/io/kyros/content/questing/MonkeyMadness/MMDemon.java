package io.kyros.content.questing.MonkeyMadness;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Position;

public class MMDemon extends NPC {

    public MMDemon(Position position) {
        super(1443, position);
        getBehaviour().setAggressive(true);
    }
}
