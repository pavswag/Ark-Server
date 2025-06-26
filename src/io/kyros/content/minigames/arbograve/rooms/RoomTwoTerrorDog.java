package io.kyros.content.minigames.arbograve.rooms;

import io.kyros.Server;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.arbograve.ArbograveRoom;
import io.kyros.content.minigames.arbograve.bosses.Leech;
import io.kyros.content.minigames.arbograve.bosses.MutantTarn;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

public class RoomTwoTerrorDog extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        Server.getGlobalObjects().add(new GlobalObject(21299, 1718, 4272, instancedArea.getHeight(), 1, 10, -1, -1).setInstance(instancedArea));
        for (int i = 0; i < 8 + (instancedArea.getPlayers().size() * 2); i++) {
            new Leech(new Position(Misc.random(1709, 1717), Misc.random(4264, 4272)),instancedArea);
        }
        return new MutantTarn(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_5th_ROOM;
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
        return new Position(1681, 4243);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
