package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;

import static io.kyros.Server.getNpcs;

public class ClickableLinkHandler implements PacketType {

    @Override
    public void processPacket(Player player, int type, int size) {
        int npcIndex = player.getInStream().readUnsignedWord();
        NPC npc = getNpcs().get(npcIndex);
        if (npc  == null)
            return;

        if (npc.getDefinition().getCombatLevel() > 0) {
            player.sendMessage("Opening drops for: "+npc.getName()+"...");
            Server.getDropManager().openForPacket(player, npc.getNpcId());
        }
    }
}
