package io.kyros.content.minigames.blastfurnance.npcs;

import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class BlastFurnaceNpc extends NPC {

    private String[] overheadChats;
    private int previousOverheadChat;
    private int overheadChatInterval;
    private int timer;
    public BlastFurnaceNpc(int npcId, Position position) {
        super(npcId, position);
    }

    public void process() {
        if (overheadChats == null) {
            return;
        }
        if (++timer % overheadChatInterval == 0) {
            sayRandomChat();
        }
    }

    public void sayRandomChat() {
        int index;
        do {
            index = Misc.trueRand(overheadChats.length);
        } while (index == previousOverheadChat);
        forceChat(overheadChats[index]);
        previousOverheadChat = index;
    }

    public void setupOverheadChat(int overheadChatInterval, String... overheadChats) {
        this.overheadChatInterval = overheadChatInterval;
        this.overheadChats = overheadChats;
    }



}
