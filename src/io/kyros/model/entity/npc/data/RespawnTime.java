package io.kyros.model.entity.npc.data;

import io.kyros.Server;
import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.bosses.wildypursuit.TheUnbearable;
import io.kyros.content.donor.DonoSlayerInstances;
import io.kyros.content.skills.hunter.trap.impl.BirdSnare;
import io.kyros.content.skills.hunter.trap.impl.BoxTrap;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;

public class RespawnTime {

    public static int get(NPC npc) {
        if (Server.isDebug()) {
            return 5;
        }
        int id = npc.getNpcId();

        for (BoxTrap.BoxTrapData boxTrap : BoxTrap.BoxTrapData.values()) {
            if (id == boxTrap.getNpcId()) {
                return boxTrap.getRespawn();
            }
        }

        for (BirdSnare.BirdData birdSnare : BirdSnare.BirdData.values()) {
            if (id == birdSnare.getNpcId()) {
                return birdSnare.getRespawn();
            }
        }

        if (Boundary.isIn(npc, DonoSlayerInstances.boundary)) {
            return 1;
        }

        return switch (id) {
            case 10956, 7649, 11775, 8781, 10531, 10532, 3353, 3358, 7817, 911, 4987, 5079 -> 2;
            case 12223 -> 5;
            case 6600, 6601, 6602, 320, 1049, 6617, 3118, 3120, 6768, Npcs.SKELETON_HELLHOUND, 2402, 2401, 2400, 2399,
                 5916, 7604, 7605, 7606, 7585, 5129, FragmentOfSeren.FRAGMENT_ID, FragmentOfSeren.NPC_ID,
                 FragmentOfSeren.CRYSTAL_WHIRLWIND, TheUnbearable.NPC_ID, 7563, 7573, 7544, 7566, 7553, 7554, 7555,
                 7560, 7527, 7528, 7529, 5001, 6477, 5462, 7858, 7859 -> -1;

            case 963, 965, 7559, 11756, 10936, 12813 -> 10;

            case 5862 -> //cerberus
                    15;//anti-santa

            case Npcs.SARACHNIS, 11278, 492 -> 20;

            case 8164, 8172 -> 25;

            case 6618, 6619, 319, 5890 -> 30;

            case 2265, 2266, 2267 -> 36;

            case 8609, 2216, 2217, 2218, 3163, 3164, 3165, 2206, 2207, 2208, 3130, 3131, 3132, 6611, 6612, 6503 -> 40;

            case 1046, 465 -> 60;

            case 2558, 2559, 2560, 2561, 2562, 2563, 2564, 2205, 2215, 3129, 3162, 1641, 1642 -> 100;

            case 1643 -> 180;

            case 1654 -> 250;

            case 3777, 3778, 3779, 3780, 7302 -> 500;

            default -> 35;
        };
    }
}
