package io.kyros.content.minigames.arbograve;

import io.kyros.content.instances.InstancedArea;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public abstract class ArbograveRoom {

    public abstract ArbograveBoss spawn(InstancedArea instancedArea);

    public abstract Position getPlayerSpawnPosition();

    public abstract Boundary getBoundary();

    public abstract boolean handleClickObject(Player player, WorldObject worldObject, int option);

    public abstract void handleClickBossGate(Player player, WorldObject worldObject);

    public abstract Position getDeathPosition();

    public abstract Position getFightStartPosition();

    public abstract boolean isRoomComplete(InstancedArea instancedArea);

}
