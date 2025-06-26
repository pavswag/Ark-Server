package io.kyros.model.entity.npc;

import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.player.Player;

import java.util.Arrays;
import java.util.function.Consumer;

public interface NPCAction {

    void handle(Player player, NPC npc);

    static void register(int npcId, int option, NPCAction action) {
        NpcDef def = NpcDef.forId(npcId);
        if(def.defaultActions == null)
            def.defaultActions = new NPCAction[5];
        def.defaultActions[option - 1] = action;
    }

    static void register(int npcId, Consumer<NPCAction[]> actionsConsumer) {
        NPCAction[] actions = new NPCAction[6];
        actionsConsumer.accept(actions);
        NpcDef.forId(npcId).defaultActions = Arrays.copyOfRange(actions, 1, actions.length);
    }
}
