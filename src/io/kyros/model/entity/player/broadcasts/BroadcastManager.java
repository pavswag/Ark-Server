package io.kyros.model.entity.player.broadcasts;

import io.kyros.Server;

import java.util.Objects;

public class BroadcastManager {

    public static Broadcast[] broadcasts = new Broadcast[100000];

    public static void removeBroadcast(int index) {
        if (broadcasts[index] != null) {
            broadcasts[index] = null;
        }
    }

    public static void addIndex(Broadcast broadcast) {
        int index = getFreeIndex();

        if (index == -1) {
            for (Broadcast broadcast1 : broadcasts) {
                removeBroadcast(broadcast1.index);
            }
            System.err.println("Error adding index.. broadcast list is full!");
            return;
        }

        broadcast.setIndex(index);

        broadcasts[index] = broadcast;

        Server.getPlayers().stream().filter(Objects::nonNull).forEach(p -> {
            if (broadcast.sendChatMessage)
                p.sendMessage(broadcast.getMessage());
            p.getPA().sendBroadCast(broadcasts[index]);
        });
    }

    public static int getFreeIndex() {
        for (int i = 0; i < broadcasts.length; i++) {
            Broadcast broadcast = broadcasts[i];
            if (broadcast == null)
                return i;
        }
        return -1;
    }
}
