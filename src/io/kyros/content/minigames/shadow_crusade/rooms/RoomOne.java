package io.kyros.content.minigames.shadow_crusade.rooms;

import io.kyros.Server;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeConstants;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeRoom;
import io.kyros.content.minigames.shadow_crusade.bosses.EliteKnight;
import io.kyros.content.minigames.shadow_crusade.bosses.EliteMage;
import io.kyros.content.minigames.shadow_crusade.bosses.EliteRanger;
import io.kyros.content.minigames.shadow_crusade.bosses.EliteWarrior;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class RoomOne extends ShadowcrusadeRoom {
    @Override
    public ShadowcrusadeBoss spawn(InstancedArea instancedArea) {
        for (int i = 0; i < 10; i++) {
            int x = Misc.random(2757, 2767);
            int y = Misc.random(4245, 4267);
            int npcID = Misc.random(13463,13464);
            if (RegionProvider.getGlobal().get(x, y).hasObject(x,y,instancedArea.getHeight())) {
                continue;
            }
            new EliteKnight(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
        }

        for (int i = 0; i < 10; i++) {
            int x = Misc.random(2757, 2767);
            int y = Misc.random(4245, 4267);
            int npcID = Misc.random(13467,13468);
            new EliteRanger(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
        }

        for (int i = 0; i < 10; i++) {
            int x = Misc.random(2757, 2778);
            int y = Misc.random(4260, 4267);
            int npcID = Misc.random(13463,13464);
            new EliteKnight(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
        }

        for (int i = 0; i < 10; i++) {
            int x = Misc.random(2757, 2778);
            int y = Misc.random(4260, 4267);
            int npcID = Misc.random(13467,13468);
            new EliteRanger(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
        }
        return new EliteRanger(13467, new Position(2772, 4263, instancedArea.getHeight()), instancedArea);
    }

    //13463 13464 (Knight)
    //13465 13466 (Warrior)
    //13467 13468 (Range)
    //13469 13470 (Mage)

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(2762, 4245, 0);
    }

    @Override
    public Boundary getBoundary() {
        return ShadowcrusadeConstants.SHADOW_1ST_ROOM;
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
        return new Position(2762, 4245, 0);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(2762, 4245, 0);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
