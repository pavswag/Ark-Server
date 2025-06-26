package io.kyros.model.entity.npc.actions;

import io.kyros.Server;
import io.kyros.content.bosses.ChaoticClone;
import io.kyros.content.bosses.Skotizo;
import io.kyros.content.bosses.SuperiorTask.SuperiorTaskInstance;
import io.kyros.content.bosses.godwars.God;
import io.kyros.content.bosses.godwars.GodwarsNPCs;
import io.kyros.content.bosses.hydra.HydraStage;
import io.kyros.content.bosses.sharathteerk.SharInstance;
import io.kyros.content.bosses.sol_heredit.SolInstance;
import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.bosses.zulrah.Zulrah;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.questing.hftd.DagannothMother;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.kyros.Server.getPlayers;
import static io.kyros.Server.npcHandler;
import static io.kyros.model.entity.player.Boundary.CORPOREAL_BEAST_LAIR;
import static io.kyros.model.entity.player.Boundary.GODWARS_BOSSROOMS;

public class NpcAggression {

    private static final int[] REVS = {7931, 7932, 7933, 7934, 7935, 7936, 7937, 7938, 7939, 7940};

    private static List<Integer> none_aggressive_npcs = Arrays.asList(
            7514//Energy orb from Mage Arena2

    );

    public static void doAggression(NPC npc, NPCHandler npcHandler) {
        if (npc.getNpcId() == 12166) {
            return;//Duke sleep ignore Aggro
        }

        if ((isAggressive(npc) || npc.getBehaviour().isAggressive())
                && (!npc.underAttack || npc.getPosition().inMulti())
                && npc.getPlayerAttackingIndex() <= 0
                && !npc.isDead()
                && !npc.isPet
                && !npc.isThrall
                && !npcHandler.switchesAttackers(npc)
                && !Boundary.isIn(npc, SuperiorTaskInstance.boundary)
                && !Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA)
                && !Boundary.isIn(npc, Boundary.BABA_ZONE)
                && !Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR) && npc.def().hasAction("attack")) {
            final Player[] closestPlayer = {null};
            final double[] closestDistance = {Integer.MAX_VALUE};
            God god = GodwarsNPCs.NPCS.get(npc.getNpcId());
            Server.getPlayers().forEach(player -> {
                if (player.heightLevel != npc.heightLevel) {
                    return;
                }
                if (Boundary.isIn(player, Boundary.RESOURCE_AREA_BOUNDARY)) {
                    return;
                }
                // Optimization
                if (player.distanceToPoint(npc.absX, npc.absY) > 26) {
                    return;
                }

                if (none_aggressive_npcs.stream().filter(Objects::nonNull).anyMatch(i -> i.intValue() == npc.getNpcId()))
                    return;

                if (player.getInstance() != npc.getInstance() || player.isIdle || player.isAggressionTimeout(player) || player.isInvisible()
                        || ((player.underAttackByNpc > 0 || player.underAttackByPlayer > 0) && !player.getPosition().inMulti())) {
                    return;
                }

                if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
                    return;
                }

                if (!Boundary.isIn(player, Boundary.GODWARS_BOSSROOMS)) {
                    if (god != null && player.getPosition().inGodwars() && player.getEquippedGodItems() != null
                            && player.getEquippedGodItems().contains(god)) {
                        return;
                    }
                }

                if(player.playerEquipment[Player.playerHands] == 21816 && player.getPosition().inWild() && Arrays.stream(REVS).anyMatch(revId -> revId == npc.getNpcId())) {
                    return;
                }

                if (!Boundary.isIn(npc, SuperiorTaskInstance.boundary)) {
                    if (player.underAttackByPlayer > 0 || player.playerAttackingIndex > 0) {
                        player.logoutDelay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3);
                        return;
                    }
                }

                if ((player.logoutDelay > System.currentTimeMillis())) {
                    return;
                }

                if(npc.getAttackBounds() != null && !npc.getAttackBounds().inside(player))
                    return;

                int distanceRequired = getAggressionDistance(npc, npcHandler.distanceRequired(npc) + (npcHandler.followDistance(npc) / 2));

//                if(npc.getNpcId() == 6607)
//                    System.out.println("Chaos Druid needs aggro distance of " + distanceRequired);
                if (Boundary.isIn(npc, Boundary.CATACOMBS) && npc.getHeight() >= 4) {
                    distanceRequired = 20;
                }

                double distance = npc.getDistance(player.absX, player.absY);
                if (distance < closestDistance[0] && distance <= distanceRequired) {
                    closestDistance[0] = distance;
                    closestPlayer[0] = player;
                }
            });

            if (closestPlayer[0] != null) {
                npc.setPlayerAttackingIndex(closestPlayer[0].getIndex());
            }
        } else if (isAggressive(npc)
                && !npc.underAttack
                && !npc.isDead()
                && !npc.isThrall
                && !npc.isPet
                && !Boundary.isIn(npc, SuperiorTaskInstance.boundary)
                && !Boundary.isIn(npc, Boundary.BABA_ZONE)
                && !Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA)
                && (npcHandler.switchesAttackers(npc) || Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) ||
                Boundary.isIn(npc, Boundary.CATACOMBS) && npc.getHeight() >= 4) && npc.def().hasAction("attack")) {

            if (System.currentTimeMillis() - npc.lastRandomlySelectedPlayer > 10000) {
                int player = getCloseRandomPlayer(npc);

                if (player > -1) {

                    if ((Server.getPlayers().get(player).underAttackByPlayer > 0 && Server.getPlayers().get(player).underAttackByNpc <= 0 || Server.getPlayers().get(player).playerAttackingIndex > 0 && Server.getPlayers().get(player).underAttackByNpc <= 0) && !Server.getPlayers().get(player).getPosition().inMulti()) {
//                        System.out.println("Player is under attack by a player or npc ignore them");
                        return;
                    }
                    if(npc.getAttackBounds() != null && !npc.getAttackBounds().inside(getPlayers().get(player)))
                        return;
                    npc.setPlayerAttackingIndex(player);
                    Server.getPlayers().get(player).underAttackByPlayer = npc.getIndex();
                    Server.getPlayers().get(player).underAttackByNpc = npc.getIndex();
                    npc.lastRandomlySelectedPlayer = System.currentTimeMillis();
                }
            }
        } else {
            if (!npc.isDead
                    && !npc.isPet
                    && !npc.isThrall
                    && !Boundary.isIn(npc, Boundary.WILDERNESS_PARAMETERS)
                    && !Boundary.isIn(npc, Boundary.DEEP_WILDY_CAVES)
                    && npc.getDefinition().getCombatLevel() > 0 && npc.getNpcId() != 539
                    && npc.def().hasAction("attack")) {
                if (System.currentTimeMillis() - npc.lastRandomlySelectedPlayer > 10000) {
                    if (npc.getInstance() != null) {
                        Player player = npc.getInstance().getPlayers().get(Misc.random(npc.getInstance().getPlayers().size()-1));
                        if (player != null) {
                            npc.setPlayerAttackingIndex(player.getIndex());
                            player.underAttackByPlayer = npc.getIndex();
                            player.underAttackByNpc = npc.getIndex();
                            npc.lastRandomlySelectedPlayer = System.currentTimeMillis();
                        }
                    } else {
                        int player = getCloseRandomPlayer(npc);

                        if (npc.getAttackBounds() != null && !npc.getAttackBounds().inside(getPlayers().get(player)))
                            return;
                        if (player > -1 && Server.getPlayers().get(player).usingInfAgro) {
//                        System.out.println("Aggro done");
                            npc.setPlayerAttackingIndex(player);
                            Server.getPlayers().get(player).underAttackByPlayer = npc.getIndex();
                            Server.getPlayers().get(player).underAttackByNpc = npc.getIndex();
                            npc.lastRandomlySelectedPlayer = System.currentTimeMillis();
                        }
                    }
                }
            } else if (!npc.isDead
                    && !npc.isPet
                    && !npc.isThrall
                    && !Boundary.isIn(npc, Boundary.WILDERNESS_PARAMETERS)
                    && !Boundary.isIn(npc, Boundary.DEEP_WILDY_CAVES)
                    && npc.getDefinition().getCombatLevel() > 0 && npc.getNpcId() != 539) {
                if (System.currentTimeMillis() - npc.lastRandomlySelectedPlayer > 10000) {
                    int player = getCloseRandomPlayer(npc);

                    if (npc.getAttackBounds() != null && !npc.getAttackBounds().inside(getPlayers().get(player)))
                        return;
                    if (player > -1 && Server.getPlayers().get(player).usingInfAgro) {
//                        System.out.println("Aggro done");
                        npc.setPlayerAttackingIndex(player);
                        Server.getPlayers().get(player).underAttackByPlayer = npc.getIndex();
                        Server.getPlayers().get(player).underAttackByNpc = npc.getIndex();
                        npc.lastRandomlySelectedPlayer = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    public static int getCloseRandomPlayer(NPC npc) {
        ArrayList<Integer> players = new ArrayList<>();
        Server.getPlayers().forEach(player -> {
            // Great Olm
            if (npc.getNpcId() == 7554 && !player.getPosition().inOlmRoom()) {
                return;
            }

            if (Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR)) {
                if (!Boundary.isIn(player, CORPOREAL_BEAST_LAIR)) {
                    npc.setPlayerAttackingIndex(0);
                    return;
                }
            }
            /**
             * Skips attacking a player if mode set to invisible
             */
            if (player.isInvisible()) {
                return;
            }
            /**
             * If player is in PVP combat, ignore
             */
            if (player.underAttackByPlayer > 0 && player.getPosition().inWild() && !player.getPosition().inMulti()) {
                return;
            }
            if (player.playerAttackingIndex > 0 && player.getPosition().inWild() && !player.getPosition().inMulti()) {
                return;
            }

            if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && !Boundary.isIn(player, GODWARS_BOSSROOMS)) {
                return;
            }

            int distanceRequired = getAggressionDistance(npc, npcHandler.distanceRequired(npc) + (npcHandler.followDistance(npc) / 2));
            if (Boundary.isIn(npc, Boundary.CATACOMBS) && npc.getHeight() >= 4) {
                distanceRequired = 40;
            }
            if (Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA)) {
                distanceRequired = 40;
            }


            if (Server.npcHandler.goodDistance(player.absX, player.absY, npc.absX, npc.absY, distanceRequired) || NPCHandler.isFightCaveNpc(npc)) {
                if ((player.underAttackByPlayer <= 0 && player.underAttackByNpc <= 0)
                        || player.getPosition().inMulti())
                    if (player.heightLevel == npc.heightLevel)
                        players.add(player.getIndex());
            }
        });
        if (!players.isEmpty()) {
            return players.get(Misc.random(players.size() - 1));
        } else {
            return -1;
        }
    }

    private static int getAggressionDistance(NPC npc, int baseDistance) {
        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS) && npc.getNpcId() > 0) {
            return 25;
        }
        if (Boundary.isIn(npc, SuperiorTaskInstance.boundary) && npc.getNpcId() > 0) {
            return 25;
        }
        if (Boundary.isIn(npc, Boundary.GODWARS_OTHER_ROOMS) && npc.getNpcId() > 0) {
            return 8;
        }
        if (Boundary.isIn(npc, Boundary.SARACHNIS_LAIR) || Boundary.isIn(npc, Boundary.MIMIC_LAIR) || Boundary.isIn(npc, Boundary.GROTESQUE_LAIR)) {
            return 25;
        }
        if (Boundary.isIn(npc, Boundary.CATACOMBS)) {
            return 5;
        }
        if (Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE1)) {
            return 5;
        }
        if (Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE2)) {
            return 5;
        }
        if (Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE3)) {
            return 5;
        }
        if (Boundary.isIn(npc, Boundary.XERIC)) {
            return 50;
        }
        if (Boundary.isIn(npc, ArbograveConstants.ALL_BOUNDARIES)) {
            return 50;
        }
        if (npc.getNpcId() > 0)
            switch (npc.getNpcId()) {
                case 7594:
                case 5630:
                case 3233:
                case 2948:
                case 7804:
                case 499:
                    return 20;
                case 11246:
                    return 15;
                case 135:
                    return 8;
                case 1257:
                    return 20;
            }
        return baseDistance;
    }

    public static boolean isAggressive(NPC npc) {
        if (Boundary.isIn(npc, Boundary.ARAXXOR_BOSS)) {
            return true;
        }

        if(HydraStage.stream().anyMatch(stage -> npc.getIndex() == stage.getNpcId()))
            return true;

        int npcId = npc.getNpcId();
        if (npcId == 2098 //hill giant
                || npcId == 7283 //npc
                || npcId == 85 //ghost
                || npcId == 7268//possessed pickaxe
                || npcId == 891//moss giant
                || npcId == 70//skeleton
                || npcId == 273//iron dragon
        ) {
            return false;
        }

        if (npc.getNpcId() >= 5886 && npc.getNpcId() <= 5891) {
            return true;
        }

        if(HydraStage.stream().anyMatch(stage -> npcId == stage.getNpcId()))
            return true;

        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)
                ||Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA)
                || Boundary.isIn(npc, SolInstance.boundary)
                || Boundary.isIn(npc, ChaoticClone.boundary)
                || Boundary.isIn(npc, Boundary.CORPOREAL_BEAST_LAIR)
                || Boundary.isIn(npc, Boundary.XERIC)
                || Boundary.isIn(npc, Boundary.RAIDROOMS)
                || Boundary.isIn(npc, Boundary.HESPORI)
                || Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE1)
                || Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE2)
                || Boundary.isIn(npc, Boundary.MOS_LEHARMLESS_CAVE3)
                || (Boundary.isIn(npc, Boundary.CATACOMBS)
                || (Boundary.isIn(npc, SuperiorTaskInstance.boundary))
                || ((Boundary.isIn(npc, Boundary.SARACHNIS_LAIR) && npc.getBehaviour().isAggressive())
                || ((Boundary.isIn(npc, Boundary.MIMIC_LAIR) && npc.getBehaviour().isAggressive()))
                || ((Boundary.isIn(npc, Boundary.GROTESQUE_LAIR) && npc.getBehaviour().isAggressive())))
        )) {
            return true;
        }

        if (Arrays.stream(DagannothMother.DAGANNOTH_MOTHER_TRANSFORMS).anyMatch(dagId -> dagId == npcId)) {
            return true;
        }

        switch (npc.getNpcId()) {
            case 12191:
            case 10956:
            case 12223:
            case 12205:
            case 12783:
            case 12821:
            case 3353:
            case 3358:
            case 499:
            case 11246:
                return true;
            case 1257:
                return true;
            case 5916:
            case 690:
            case 963:
            case 965:
            case 955:
            case 957:
            case 959:
            case 7032:
            case 5867:
            case 5868:
            case 5869:
            case 2042:
            case 239:
            case 7413:
            case 1739:
            case 1740:
            case 1741:
            case 1742:
            case 2044:
            case 2043:
            case 465:
            case Zulrah.SNAKELING:
            case Npcs.SKELETON_HELLHOUND:
            case 6611:
            case 8164:
            case 8172:
            case 6612:
            case 6610:
            case 494:
            case 5535:
            case 2550:
            case 2551:
            case 50:
            case 28:
            case 2552:
            case 6503:
            case 2553:
            case 2558:
            case 2559:
            case 2560:
            case 2561:
            case 2562:
            case 2563:
            case 2564:
            case 2565:
            case 2892:
            case 2894:
            case 2265:
            case 2266:
            case 2267:
            case 2035:
            case 5779:
            case 291:
            case 435:
            case 135:
            case 484:
            case 7276:
            case 5944: // Rock lobster

                // Godwars
            case 3138:
            case 2205:
            case 2206:
            case 2207:
            case 2208:
            case 2209:
            case 2211:
            case 2212:
            case 2215:
            case 2216:
            case 2217:
            case 2218:
            case 2233:
            case 2234:
            case 2235:
            case 2237:
            case 2242:
            case 2243:
            case 2244:
            case 2245:
            case 3129:
            case 3130:
            case 3131:
            case 3132:
            case 3133:
            case 3134:
            case 3135:
            case 3137:
            case 3139:
            case 3140:
            case 3141:
            case 3159:
            case 3160:
            case 3161:
            case 3162:
            case 7037:
            case 3163:
            case 3164:
            case 3165:
            case 3166:
            case 3167:
            case 1543:
                return true;
            case 3168:
            case 3174:

            case Skotizo.SKOTIZO_ID:
            case Skotizo.REANIMATED_DEMON:
            case Skotizo.DARK_ANKOU:

                // Barrows tunnel monsters
            case 1678:
            case 1679:
            case 1683:
            case 1684:
            case 1685:
                // GWD
            case 6230:
            case 6231:
            case 6229:
            case 6232:
            case 6240:
            case 6241:
            case 6242:
            case 6233:
            case 6234:
            case 6243:
            case 6244:
            case 6245:
            case 6246:
            case 6238:
            case 6239:
            case 6625:
            case 122:// Npcs That Give BandosKC
            case 6278:
            case 6277:
            case 6276:
            case 6283:
            case 6282:
            case 6281:
            case 6280:
            case 6279:
            case 6271:
            case 6272:
            case 6273:
            case 6274:
            case 6269:
            case 6270:
            case 6268:
            case 6221:
            case 6219:
            case 6220:
            case 6217:
            case 6216:
            case 6215:
            case 6214:
            case 6213:
            case 6212:
            case 6211:
                return true;
            case 6218:
            case 6275:
            case 6257:// Npcs That Give SaraKC
            case 6255:
            case 6256:
            case 6259:
            case 6254:
            case 1689:
            case 1694:
            case 1699:
            case 1704:
            case 1709:
            case 1714:
            case 1724:
            case 1734:
            case 6914: // Lizardman, Lizardman brute
            case 6915:
            case 6916:
            case 6917:
            case 6918:
            case 6919:
            case 6766:
            case 7573:
            case 7617: // Tekton magers
            case 7544: // Tekton
            case 7604: // Skeletal mystic
            case 7605: // Skeletal mystic
            case 7606: // Skeletal mystic
            case 5129:
            case FragmentOfSeren.NPC_ID:
            case 7388: // Start of superior
            case 7389:
            case 7390:
            case 7391:
            case 7392:
            case 7393:
            case 7394:
            case 7395:
            case 7396:
            case 7397:
            case 7398:
            case 7399:
            case 7400:
            case 7401:
            case 7402:
            case 7403:
            case 7404:
            case 7405:
            case 7406:
            case 7407:
            case 7409:
            case 7410:
            case 7411: // end of superior
            case 1443:
                return true;
            case 1524:
            case 6600:
            case 6601:
            case 6602:
            case 1049:
            case 6617:
            case 6620:
            case 2241:
            case FragmentOfSeren.FRAGMENT_ID:
            case FragmentOfSeren.CRYSTAL_WHIRLWIND:
                return false;
            case 6319:
            case 8060:
                return true;
        }
        if (npc.getPosition().inWild() && npc.getHealth().getMaximumHealth() > 0)
            return true;
        if (npc.inRaids() && npc.getHealth().getMaximumHealth() > 0)
            return true;
        if (npc.inXeric() && npc.getHealth().getMaximumHealth() > 0)
            return true;
        return NPCHandler.isFightCaveNpc(npc);
    }


}
