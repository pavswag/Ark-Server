package io.kyros.content.minigames.tob.rooms;

import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.content.minigames.tob.TobRoom;
import io.kyros.content.minigames.tob.bosses.Xarpus;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;

public class RoomFiveXarpus extends TobRoom {

    @Override
    public Xarpus spawn(InstancedArea instancedArea) {
        return new Xarpus(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3170, 4375, 1);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (worldObject.getY() < 4392) {        // South
            if (player.getY() >= 4380) {
                player.getPA().movePlayer(player.getX(), 4378, player.getHeight());
            } else {
                player.getPA().movePlayer(player.getX(), 4380, player.getHeight());
            }
        } else {                                // North
            if (player.getY() >= 4396) {
                player.getPA().movePlayer(player.getX(), 4394, player.getHeight());
            } else {
                player.getPA().movePlayer(player.getX(), 4396, player.getHeight());
            }
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TobConstants.XARPUS_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3170, 4378, 1);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3170, 4380, 1);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3169, 4398, 1), 1);
    }
}
