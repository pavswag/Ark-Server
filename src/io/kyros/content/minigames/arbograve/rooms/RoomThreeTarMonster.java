package io.kyros.content.minigames.arbograve.rooms;

import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveBoss;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.arbograve.ArbograveRoom;
import io.kyros.content.minigames.arbograve.bosses.Leech;
import io.kyros.content.minigames.arbograve.bosses.TarMonster;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import static io.kyros.content.minigames.arbograve.ArbograveConstants.tarMonsterSpawns;

public class RoomThreeTarMonster extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        for (int[] tarMonsterSpawn : tarMonsterSpawns) {
            new TarMonster(new Position(tarMonsterSpawn[0], tarMonsterSpawn[1], 0), instancedArea);
        }
        for (int i = 0; i < 8 + (instancedArea.getPlayers().size() * 2); i++) {
            new Leech(new Position(Misc.random(1699, 1709), Misc.random(4250, 4255)),instancedArea);
        }
        return null;
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1711, 4253);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_3rd_ROOM;
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
        return new Position(1711, 4253);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1711, 4253);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
