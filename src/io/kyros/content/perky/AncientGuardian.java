package io.kyros.content.perky;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Position;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 11/03/2024
 */
public class AncientGuardian extends NPC {
    public AncientGuardian(int npcId, Position position) {
        super(npcId, position);
        this.getBehaviour().setRespawn(false);
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
    }


}
