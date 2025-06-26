package io.kyros.content.games;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.model.Animation;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.Arrays;

public class PartyRoom {

    private static boolean KNIGHTS_DANCING;
    private static boolean BALLOONS_SPAWNED;

    private final NPC[] knight = new NPC[8];
    private final String[] transcript = new String[] { "We're Knights of the Party Room",
            "We dance round and round like a loon",
            "Quite often we like to sing",
            "Unfortunately we make a din",
            "We're Knights of the Prty Room",
            "Do you like our helmet plumes?",
            "everyone's happy now we can move",
            "Like a party animal in the groove" };

    private void performEmote(int id) {
        for (NPC npc : knight) {
            if (npc == null || npc.isDead())
                continue;
            npc.startAnimation(new Animation(id));
        }
    }

    private void talk(String message) {
        for (NPC npc : knight) {
            if (npc == null || npc.isDead())
                continue;
            npc.forceChat(message);
        }
    }

    public void run() {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {

                // set the boolean to true if the knights are spawned, so players can't keep activating the feature
                KNIGHTS_DANCING = true;
                BALLOONS_SPAWNED = true;
                // spawning the knights
                for (int i = 0; i < knight.length; i++)
                    knight[i] = NPCSpawning.spawnNpc(4771, 3042 +i, 3378,0,0,0);

                // performing the first emote before the cycle
                performEmote(866);

                if (container.getTotalTicks() % 4 == 0) {
                    performEmote(866);
                }

                if (container.getTotalTicks() < transcript.length) {
                    talk(transcript[container.getTotalTicks()]);
                }

                if (container.getTotalTicks() % 8 == 0) {
                    talk("Thanks for now, ArkCane!");
                    performEmote(858);
                }

                if (container.getTotalTicks() == 200) {
                    Arrays.stream(knight).forEach(npc -> {
                        npc.unregister();
                        npc.appendDamage(999, HitMask.HIT);
                    });
                    KNIGHTS_DANCING = false;
                    BALLOONS_SPAWNED = false;
                    container.stop();
                }
                    DropBallons();

/*                    int x1 = 3037, x2 = 3054;
                    int y1 = 3372, y2 = 3384;

                    for (int x = x1; x < x2; x++) {
                        for (int y = y1; y < y2; y++) {
                            if (container.getTotalTicks() % 2 == 0) {
                                if (RegionProvider.getGlobal().isBlocked(x,y,0))
                                    continue;
                                Server.getGlobalObjects().add(new GlobalObject(Misc.random(115, 122), x, y, 0, 0, 10, 120));
                            }
                        }
                    }*/
            }
        },2);
    }

    private void DropBallons() {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int x1 = 3037, x2 = 3054;
                int y1 = 3372, y2 = 3384;
                int x = Misc.random(x1,x2);
                int y = Misc.random(y1,y2);
                if (!RegionProvider.getGlobal().isBlocked(x,y,0) && !Server.getGlobalObjects().anyExists(x,y,0)) {
                    Server.getGlobalObjects().add(new GlobalObject(Misc.random(115, 122), x, y, 0, 0, 10, 120));
                }
                if (container.getTotalTicks() == 60) {
                    container.stop();
                }
            }

            },3);

    }


}
