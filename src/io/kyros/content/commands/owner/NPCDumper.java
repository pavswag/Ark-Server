package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

public class NPCDumper extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        Server.getNpcs().forEach(npc -> {
            if (Boundary.getWildernessLevel(npc.getX(), npc.getY()) > 0) {
                for (GameItem allNPCdrop : Server.getDropManager().getAllNPCdrops(npc.getNpcId())) {
                    System.out.println("NPC ID : " + allNPCdrop.getId()+ ", " +allNPCdrop.getDef().getName() +", " + allNPCdrop.rarity);
                }
            }
        });
    }

}
