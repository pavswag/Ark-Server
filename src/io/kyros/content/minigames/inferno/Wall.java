package io.kyros.content.minigames.inferno;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Position;

/**
 * @author Arthur Behesnilian 10:22 PM
 */
public class Wall extends NPC {

    public Wall(Position position, Inferno infernoInstance) {
        super(7710, position);
        this.setNpcStats(NPCSpawning.getStats(1000, 0, 0));
        this.walkingType = 0;
        this.setInstance(infernoInstance);
    }

    @Override
    public void onDeath() {
        super.onDeath();
        this.getRegionProvider().removeNpcClipping(this);
    }
}
