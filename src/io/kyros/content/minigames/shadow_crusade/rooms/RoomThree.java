package io.kyros.content.minigames.shadow_crusade.rooms;

import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeConstants;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeRoom;
import io.kyros.content.minigames.shadow_crusade.bosses.RoomThreeElementalBoss;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class RoomThree extends ShadowcrusadeRoom {
    @Override
    public ShadowcrusadeBoss spawn(InstancedArea instancedArea) {
        return new RoomThreeElementalBoss(13528, new Position(2801, 4270, instancedArea.getHeight()), instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(2801, 4260, 0);
    }

    @Override
    public Boundary getBoundary() {
        return ShadowcrusadeConstants.SHADOW_3RD_ROOM;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {

    }

    @Override
    public Position getDeathPosition() {
        return new Position(2801, 4260, 0);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(2801, 4260, 0);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}