package io.kyros.content.minigames.shadow_crusade.rooms;

import io.kyros.Server;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeBoss;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeConstants;
import io.kyros.content.minigames.shadow_crusade.ShadowcrusadeRoom;
import io.kyros.content.minigames.shadow_crusade.bosses.*;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class RoomFour extends ShadowcrusadeRoom {
    @Override
    public ShadowcrusadeBoss spawn(InstancedArea instancedArea) {
        for (int i = 0; i < 20; i++) {
            int x = Misc.random(2771, 2798);
            int y = Misc.random(4283, 4290);
            int npcID = Misc.random(13463,13470);
            if (npcID == 13463 || npcID == 13464) {
                new EliteKnight(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13465 || npcID == 13466) {
                new EliteWarrior(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13467 || npcID == 13468) {
                new EliteRanger(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13469 || npcID == 13470) {
                new EliteMage(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            }
        }

        for (int i = 0; i < 20; i++) {
            int x = Misc.random(2771, 2778);
            int y = Misc.random(4283, 4320);
            int npcID = Misc.random(13463,13470);
            if (npcID == 13463 || npcID == 13464) {
                new EliteKnight(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13465 || npcID == 13466) {
                new EliteWarrior(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13467 || npcID == 13468) {
                new EliteRanger(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            } else if (npcID == 13469 || npcID == 13470) {
                new EliteMage(npcID, new Position(x, y, instancedArea.getHeight()), instancedArea);
            }
        }

        return null;
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(2802, 4284, 0);
    }

    @Override
    public Boundary getBoundary() {
        return ShadowcrusadeConstants.SHADOW_4TH_ROOM;
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
        return new Position(2802, 4284, 0);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(2802, 4284, 0);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}