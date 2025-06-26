package io.kyros.content.seasons;

import io.kyros.Server;
import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.commands.helper.vboss;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.collisionmap.doors.Location;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Halloween {

    private static boolean Running = false;

    public static void initHalloween() {
        if (isHalloween() && !Running) {
            Running = true;
            addObjects();
            handleNpcSpawn(false);
        } else if (!isHalloween() && Running) {
            Running = false;
            removeObjects();
            handleNpcSpawn(true);
        }
    }

    public static boolean isHalloween() {
        LocalDate currentdate = LocalDate.now();
        return currentdate.getMonth().equals(Month.OCTOBER);
    }

    private static void addObjects() {
        Server.getGlobalObjects().add(new GlobalObject(46300,3099,3502,0,3,11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46300, 3103, 3502, 0, 0, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46300, 3095, 3489, 0, 2, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46300, 3095, 3493, 0, 3, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46300, 3104, 3485, 0, 2, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46300, 3108, 3485, 0, 1, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46307, 3116, 3486, 0, 0, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46255, 3119, 3480, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46261, 3105, 3496, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46261, 3105, 3493, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46268, 3099, 3499, 0, 0, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46273, 3101, 3482, 0, 1, 11,-1));
        Server.getGlobalObjects().add(new GlobalObject(46261, 3119, 3484, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46268, 3119, 3478, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(40385, 3096, 3494, 0, 0, 10,-1));

        Server.getGlobalObjects().add(new GlobalObject(46255, 3153, 3629, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46261, 3149, 3631, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46261, 3133, 3633, 0, 3, 10,-1));

        Server.getGlobalObjects().add(new GlobalObject(46260, 3137, 3620, 0, 0, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(46260, 3132, 3620, 0, 0, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(40385, 3137, 3628, 0, 2, 10,-1));
    }
    private static void removeObjects() {
        Server.getGlobalObjects().remove(new GlobalObject(46300,3099,3502,0,3,11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46300, 3103, 3502, 0, 0, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46300, 3095, 3489, 0, 2, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46300, 3095, 3493, 0, 3, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46300, 3104, 3485, 0, 2, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46300, 3108, 3485, 0, 1, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46307, 3116, 3486, 0, 0, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46255, 3119, 3480, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46261, 3105, 3496, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46261, 3105, 3493, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46268, 3099, 3499, 0, 0, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46273, 3101, 3482, 0, 1, 11,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46261, 3119, 3484, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46268, 3119, 3478, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(40385, 3096, 3494, 0, 0, 10,-1));

        Server.getGlobalObjects().remove(new GlobalObject(46255, 3153, 3629, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46261, 3149, 3631, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46261, 3133, 3633, 0, 3, 10,-1));

        Server.getGlobalObjects().remove(new GlobalObject(46260, 3137, 3620, 0, 0, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(46260, 3132, 3620, 0, 0, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(40385, 3137, 3628, 0, 2, 10,-1));
    }

    private static void handleNpcSpawn(boolean remove) {
        if (!remove) {
            for (Position spawn : spawns) {
                NPC pumpkin = NPCSpawning.spawn(8368,spawn.getX(), spawn.getY(), 0, 1, 5, true);
                pumpkin.getCombatDefinition().setAggressive(true);
                pumpkin.getBehaviour().setAggressive(true);
                pumpkin.spawnedBy = 0;
            }
            NPC elfin_home = NPCSpawning.spawn(539, 3117, 3484, 0, 1, 0, false);
            elfin_home.spawnedBy = 0;
            NPC elfin_ferox = NPCSpawning.spawn(539, 3152, 3628, 0, 1, 0, false);
            elfin_ferox.spawnedBy = 0;
        } else {
            for(NPC npc : Server.getNpcs().toNpcArray()) {
                if (npc != null) {
                    if (npc.getNpcId() == 8368 || npc.getNpcId() == 7633 || npc.getNpcId() == 7623 || npc.getNpcId() == 539) {
                        npc.getBehaviour().setRespawn(false);
                        npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
                    }
                }
            }
        }
    }

    public static int[] Cauldrons = {46255, 46256, 46257, 46258, 46259};

    private static int CurrentCauldron = 46255;

    private static long CurrentCandy = 4_270_000_000L;
    public static int pulseBoost = 0;

    private static int[] Candies = {24980, 24981, 24982, 24983, 24984, 24985, 24986, 24987, 24988};

    public static boolean handleCauldron(Player player, int option, int obj) {
        if (obj == 40385) {
            if (System.currentTimeMillis() > player.candyTimer) {
                player.getItems().addItem(24984, 10000);
                player.candyTimer = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(2));
                player.start(new DialogueBuilder(player).statement("Make sure to return every 2hours for more free candies!!"));
            } else {
//                long hour = TimeUnit.MILLISECONDS.toHours((player.candyTimer - System.currentTimeMillis()));
//                long minutes = TimeUnit.MILLISECONDS.toMinutes((player.candyTimer - System.currentTimeMillis()));
//                long seconds = TimeUnit.MILLISECONDS.toSeconds((player.candyTimer - System.currentTimeMillis()));
                Duration duration = Duration.ofMillis(player.candyTimer - System.currentTimeMillis());
                long seconds = duration.getSeconds();
                long HH = seconds / 3600;
                long MM = (seconds % 3600) / 60;
                long SS = seconds % 60;

                String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
                player.start(new DialogueBuilder(player).statement("You need to wait " + timeInHHMMSS, "before getting more candy!"));

            }
            return true;
        }

        HashMap<Integer, Integer> currentCandies = new HashMap<>();
        int totalCandies = 0;
        for (int cauldron : Cauldrons) {
            if (cauldron == obj) {
                switch (option) {
                    case 1:
                        for (int candy : Candies) {
                            if (player.getItems().getInventoryCount(candy) > 0) {
                                currentCandies.put(candy, player.getItems().getInventoryCount(candy));

                                totalCandies += player.getItems().getInventoryCount(candy);
                            }
                        }

                        if (totalCandies <= 0) {
                            return true;
                        }

                        currentCandies.forEach((itemid, count) -> player.getItems().deleteItem2(itemid, count));
                        CurrentCandy += totalCandies;
                        pulseBoost += totalCandies;
                        globalBoost += totalCandies;
                        player.sendMessage("@or1@[@gre@Halloween@or1@] @red@You have just deposited a total of " + Misc.formatCoins(totalCandies) + " candies to the Great cauldron!");
                        handleCandyCount();
                        handleBuckets(player);
                        return true;
                    case 2:
                        player.sendMessage("@or1@[@gre@Halloween@or1@] @red@Currently the Great cauldron holds " + Misc.formatCoins(CurrentCandy) + " candies!" );
                        return true;
                    case 3:
                        int[] buckets = {27463, 27465, 27467, 27469, 27471};
                        for (int bucket : buckets) {
                            if (player.getItems().hasInBank(bucket)) {
                                player.sendMessage("You already have a Treat cauldron, go get some candies!");
                                return true;
                            }
                        }
                        if (player.getItems().hasItemOnOrInventory(27463, 27465, 27467, 27469, 27471) || player.getItems().hasAnywhere(27463, 27465, 27467, 27469, 27471)) {
                            player.sendMessage("You already have a Treat cauldron, go get some candies!");
                            return true;
                        }

                        player.getItems().addItemUnderAnyCircumstance(27463, 1);
                        player.sendMessage("You have been given a Treat cauldron!");
                        return true;
                }
            }
        }
        return false;
    }

    public static boolean handleBucket(Player player, int item) {
        int[] buckets = {27463, 27465, 27467, 27469, 27471};
        for (int bucket : buckets) {
            if (bucket == item) {
                player.sendMessage("@or1@[@gre@Halloween@or1@] @red@Currently the Great cauldron holds " + Misc.formatCoins(CurrentCandy) + " candies!" );
                return true;
            }
        }
        return false;
    }

    public static int CauldronStage = 0;

    private static long globalBoost = 0;

    public static boolean globalBossActive = false;
    private static void handleCandyCount() {
        if (CurrentCauldron == 46255 & CurrentCandy >= 100_000_000) {
            changeCauldron();
            handleRandomBoost();
            CauldronStage = 1;
        } else if (CurrentCauldron == 46256 & CurrentCandy >= 500_000_000) {
            changeCauldron();
            handleRandomBoost();
            CauldronStage = 2;
        } else if (CurrentCauldron == 46257 & CurrentCandy >= 1_000_000_000L) {
            changeCauldron();
            handleRandomBoost();
            CauldronStage = 3;
        } else if (CurrentCauldron == 46258 & CurrentCandy >= 5_000_000_000L) {
            changeCauldron();
            handleRandomBoost();
            CauldronStage = 4;
            PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ The Great Cauldron has been filled!");
        }

        if (pulseBoost >= 5_000_000 && !isBoostRunning() && !BoostActive)  {
            handleRandomBoost();
            pulseBoost = 0;
        }

        if (globalBoost >= 10_000_000 && !globalBossActive) {
            HalloweenBoss.spawnKraken();
            globalBoost = 0;
        }
    }

    private static void changeCauldron() {
        Server.getGlobalObjects().remove(new GlobalObject(CurrentCauldron, 3119, 3480, 0, 1, 10,-1));
        Server.getGlobalObjects().remove(new GlobalObject(CurrentCauldron, 3153, 3629, 0, 1, 10,-1));
        CurrentCauldron++;
        Server.getGlobalObjects().add(new GlobalObject(CurrentCauldron, 3119, 3480, 0, 1, 10,-1));
        Server.getGlobalObjects().add(new GlobalObject(CurrentCauldron, 3153, 3629, 0, 1, 10,-1));
    }

    public static void handleCandyDrop(Player player, NPC npc) {
        Location location = npc.getLocation();

        int amt = 1;

        if (Boundary.isIn(player, Boundary.WILDERNESS_PARAMETERS) || Boundary.isIn(npc, Boundary.WILDERNESS_PARAMETERS)) {
            amt += 1;
        }
        if (DoubleCandies) {
            amt += 1;
        }
        if (player.playerEquipment[Player.playerAmulet] == 24780 && Misc.random(0, 100) == 1) {
            player.sendMessage("@red@Your blood fury shrine's, granting more candy!");
            amt++;
        }

        if (npc.getNpcId() == 8368) {
            Server.itemHandler.createGroundItem(player, 24984, location.getX(), location.getY(), location.getZ(), 1_500 * amt, player.getIndex(), false);
        } else if (npc.getNpcId() == 7633) {
            Server.itemHandler.createGroundItem(player, 24984, location.getX(), location.getY(), location.getZ(), Misc.random(10_000, 100_000) * amt, player.getIndex(), false);
        } else if (npc.getNpcId() == 7623) {
            Server.itemHandler.createGroundItem(player, 24984, location.getX(), location.getY(), location.getZ(), Misc.random(500000, 1000000) * amt, player.getIndex(), false);
            int amtz = 50;
            if (player.getItems().hasItemOnOrInventory(33150) || (player.hasFollower && (player.petSummonId == 33159))) {
                amtz = 250;
            }
            player.setSeasonalPoints(player.getSeasonalPoints() + amtz);
        }
    }

    public static boolean DoubleCandies, DoubleDivision, DoubleXP, DoubleAchieve, DoubleGroot, DoubleDiscord, TripleWGToruney;

    public static boolean[] boosts = {DoubleCandies, DoubleDivision, DoubleXP, DoubleAchieve, DoubleGroot, DoubleDiscord, TripleWGToruney};
    private static String[] Boosts ={"candies", "division", "xp", "achievements", "grootpoints", "discord", "tourney",
            "vboss", "dboss", "groot", "ultra",
            "candies", "division", "xp", "achievements", "grootpoints", "discord", "tourney"};
    private static void handleRandomBoost() {
        String boost = Boosts[Misc.random(Boosts.length-1)];
        switch (boost) {
            case "candies":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Double Candies are active for 1 hour!");
                DoubleCandies = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "division":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Double Division Pass Points are active for 1 hour!");
                DoubleDivision = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "xp":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Double XP has been activated for 1 hour!");
                DoubleXP = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "achievements":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Double Achievement points are active for 1 hour!");
                DoubleAchieve = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "grootpoints":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Double Groot points are active for 1 hour!");
                DoubleGroot = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "discord":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Bonus Discord point's has been activated for 1 hour!");
                DoubleDiscord = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "tourney":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Tournament & WG are granting extra points!");
                TripleWGToruney = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                BoostActive = true;
                break;
            case "vboss":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Vote Boss has been spawned!");
                vboss.spawnBoss();
                break;
            case "dboss":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Donor Boss has been spawned!");
                dboss.spawnBoss();
                break;
            case "groot":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Groot has been spawned!");
                Groot.spawnGroot();
                break;
            case "ultra":
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ Everyone has been given an ultra box!");
                Server.getPlayers().forEach(p -> p.getItems().addItemUnderAnyCircumstance(13346,1));
                break;
        }
    }

    private static long boostTimer = 0;

    private static boolean isBoostRunning() {
        return boostTimer > System.currentTimeMillis();
    }

    public static boolean BoostActive = false;

    public static void boostCheck() {
        if (isHalloween()) {
            if (boostTimer > 0 && !isBoostRunning() && BoostActive) {
                PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@ There is no Cauldron boost active!");
                DoubleCandies = false;
                DoubleDivision = false;
                DoubleXP = false;
                DoubleAchieve = false;
                DoubleGroot = false;
                DoubleDiscord = false;
                TripleWGToruney = false;
                BoostActive = false;
            }
        }
    }

    public static String getActiveBoost() {//17 chars max

        if (DoubleCandies) {
            return "Double Candies";
        }
        if (DoubleDivision) {
            return "Division Points++";
        }
        if (DoubleXP) {
            return "Double XP";
        }
        if (DoubleAchieve) {
            return "Achieve Points++";
        }
        if (DoubleGroot) {
            return "Groot Points++";
        }
        if (DoubleDiscord) {
            return "Discord Points++";
        }
        if (TripleWGToruney) {
            return "WG/Tourn Points++";
        }
        if (Christmas.isChristmas() && Christmas.isBoostRunning() && Christmas.candies > 5_000_000) {
            return "Cooldown Period";
        }

        return "No Candies";
    }

    public static void handleBuckets(Player player) {
        int[] buckets = {27463, 27465, 27467, 27469};
        for (int bucket : buckets) {
            if (CauldronStage == 1) {
                    if (player.getItems().hasAnywhere(bucket)) {
                        player.getItems().deleteItem(bucket, 1);
                        player.getBank().RemoveBankTabItem(bucket);
                        player.getItems().addItem(27465,1);
                    }
            } else if (CauldronStage == 2) {
                    if (player.getItems().hasAnywhere(bucket)) {
                        player.getItems().deleteItem(bucket, 1);
                        player.getBank().RemoveBankTabItem(bucket);
                        player.getItems().addItem(27467,1);
                    }
            } else if (CauldronStage == 3) {
                    if (player.getItems().hasAnywhere(bucket)) {
                        player.getItems().deleteItem(bucket, 1);
                        player.getBank().RemoveBankTabItem(bucket);
                        player.getItems().addItem(27469,1);
                    }
            } else if (CauldronStage == 4) {
                    if (player.getItems().hasAnywhere(bucket)) {
                        player.getItems().deleteItem(bucket, 1);
                        player.getBank().RemoveBankTabItem(bucket);
                        player.getItems().addItem(27471,1);
                    }
            }
        }
    }

    private static final Position[] spawns = {
            new Position(2320, 3632, 0),
            new Position(2305, 3556, 0),
            new Position(2456, 3541, 0),
            new Position(2485, 3351, 0),
            new Position(2518, 3403, 0),
            new Position(2534, 3436, 0),
            new Position(2518, 3523, 0),
            new Position(2526, 3588, 0),
            new Position(2586, 3605, 0),
            new Position(2604, 3638, 0),
            new Position(2655, 3630, 0),
            new Position(2701, 3626, 0),
            new Position(2710, 3720, 0),
            new Position(2686, 3590, 0),
            new Position(2672, 3525, 0),
            new Position(2646, 3523, 0),
            new Position(2725, 3504, 0),
            new Position(2747, 3472, 0),
            new Position(2783, 3464, 0),
            new Position(2773, 3476, 0),
            new Position(2747, 3458, 0),
            new Position(2730, 3438, 0),
            new Position(2700, 3443, 0),
            new Position(2653, 3443, 0),
            new Position(2617, 3385, 0),
            new Position(2601, 3381, 0),
            new Position(2567, 3388, 0),
            new Position(2545, 3404, 0),
            new Position(2555, 3341, 0),
            new Position(2594, 3330, 0),
            new Position(2649, 3233, 0),
            new Position(2633, 3212, 0),
            new Position(2618, 3190, 0),
            new Position(2573, 3186, 0),
            new Position(2627, 3124, 0),
            new Position(2626, 3080, 0),
            new Position(2528, 3095, 0),
            new Position(2512, 3100, 0),
            new Position(2482, 3072, 0),
            new Position(2457, 3096, 0),
            new Position(2439, 3056, 0),
            new Position(2423, 3052, 0),
            new Position(2408, 3053, 0),
            new Position(2378, 3055, 0),
            new Position(2349, 3055, 0),
            new Position(2500, 3193, 0),
            new Position(2537, 3210, 0),
            new Position(2473, 3133, 0),
            new Position(2569, 3134, 0),
            new Position(2764, 3170, 0),
            new Position(2779, 3130, 0),
            new Position(2805, 3121, 0),
            new Position(2869, 3111, 0),
            new Position(2764, 3091, 0),
            new Position(2845, 3078, 0),
            new Position(2777, 3021, 0),
            new Position(2833, 3022, 0),
            new Position(2886, 3053, 0),
            new Position(2804, 2980, 0),
            new Position(2869, 2988, 0),
            new Position(2863, 2962, 0),
            new Position(2793, 2930, 0),
            new Position(2848, 2927, 0),
            new Position(2919, 2923, 0),
            new Position(3000, 3131, 0),
            new Position(2997, 3200, 0),
            new Position(3001, 3222, 0),
            new Position(3001, 3241, 0),
            new Position(2967, 3222, 0),
            new Position(2934, 3219, 0),
            new Position(2953, 3276, 0),
            new Position(2998, 3269, 0),
            new Position(2958, 3300, 0),
            new Position(2986, 3293, 0),
            new Position(2996, 3303, 0),
            new Position(2941, 3352, 0),
            new Position(2926, 3341, 0),
            new Position(2912, 3379, 0),
            new Position(2959, 3404, 0),
            new Position(2996, 3405, 0),
            new Position(3066, 3428, 0),
            new Position(2959, 3429, 0),
            new Position(2919, 3431, 0),
            new Position(2886, 3461, 0),
            new Position(2915, 3506, 0),
            new Position(3151, 3423, 0),
            new Position(3196, 3435, 0),
            new Position(3289, 3427, 0),
            new Position(3211, 3374, 0),
            new Position(3255, 3340, 0),
            new Position(3033, 3285, 0),
            new Position(3073, 3277, 0),
            new Position(3098, 3233, 0),
            new Position(3221, 3263, 0),
            new Position(3240, 3325, 0),
            new Position(3290, 3349, 0),
            new Position(3293, 3259, 0),
            new Position(3312, 3210, 0),
            new Position(3324, 3172, 0),
            new Position(3303, 3143, 0),
            new Position(3301, 2800, 0),
            new Position(3305, 2761, 0),
            new Position(3291, 2760, 0),
            new Position(3495, 3490, 0),
            new Position(3542, 3297, 0),
            new Position(3488, 3288, 0),
            new Position(3486, 3217, 0),
            new Position(3495, 3216, 0),
            new Position(3500, 3217, 0),
            new Position(3507, 3216, 0),
            new Position(3531, 3219, 0),
            new Position(3487, 3282, 0),
            new Position(3490, 3291, 0),
            new Position(3485, 3292, 0),
            new Position(3490, 3284, 0),
            new Position(3554, 3312, 0),
            new Position(3503, 3222, 0),
            new Position(3484, 3222, 0),
            new Position(3515, 3519, 0),
            new Position(3658, 3501, 0),
            new Position(3664, 3501, 0),
            new Position(3663, 3496, 0),
            new Position(3658, 3497, 0),
            new Position(3659, 3492, 0),
            new Position(3663, 3491, 0),
            new Position(3664, 3487, 0),
            new Position(3245, 3504, 0),
            new Position(3173, 3389, 0),
            new Position(3021, 3607, 0),
            new Position(2980, 3633, 0),
            new Position(3078, 3618, 0),
            new Position(3084, 3684, 0),
            new Position(3068, 3729, 0),
            new Position(3046, 3760, 0),
            new Position(3001, 3730, 0),
            new Position(3013, 3816, 0),
            new Position(2995, 3824, 0),
            new Position(2994, 3869, 0),
            new Position(3035, 3885, 0),
            new Position(3101, 3897, 0),
            new Position(3121, 3897, 0),
            new Position(3121, 3893, 0),
            new Position(3125, 3889, 0),
            new Position(3131, 3889, 0),
            new Position(3140, 3886, 0),
            new Position(3132, 3882, 0),
            new Position(3137, 3890, 0),
            new Position(3130, 3897, 0),
            new Position(3126, 3893, 0),
            new Position(3145, 3896, 0),
            new Position(3149, 3890, 0),
            new Position(3144, 3889, 0),
            new Position(3148, 3881, 0),
            new Position(3151, 3893, 0),
            new Position(3143, 3884, 0),
            new Position(3127, 3884, 0),
            new Position(3136, 3878, 0),
            new Position(3141, 3882, 0),
            new Position(3142, 3892, 0),
            new Position(3219, 3890, 0),
            new Position(3228, 3891, 0),
            new Position(3223, 3896, 0),
            new Position(3226, 3888, 0),
            new Position(3220, 3890, 0),
            new Position(3217, 3898, 0),
            new Position(3236, 3893, 0),
            new Position(3234, 3887, 0),
            new Position(3283, 3895, 0),
            new Position(3312, 3893, 0),
            new Position(3340, 3908, 0),
            new Position(3267, 3784, 0),
            new Position(3247, 3776, 0),
            new Position(3236, 3768, 0),
            new Position(3238, 3748, 0),
            new Position(3276, 3733, 0),
            new Position(3321, 3743, 0),
            new Position(3364, 3737, 0),
            new Position(3366, 3757, 0),
            new Position(3368, 3727, 0),
            new Position(3379, 3743, 0),
            new Position(3356, 3741, 0),
            new Position(3370, 3735, 0),
            new Position(3371, 3748, 0),
            new Position(3290, 3713, 0),
            new Position(3340, 3700, 0),
            new Position(3347, 3686, 0),
            new Position(3347, 3666, 0),
            new Position(3340, 3639, 0),
            new Position(3269, 3672, 0),
            new Position(3268, 3691, 0),
            new Position(3269, 3698, 0),
            new Position(3237, 3659, 0),
            new Position(3220, 3653, 0),
            new Position(3204, 3650, 0),
            new Position(3142, 3703, 0),
            new Position(3118, 3705, 0),
            new Position(3097, 3699, 0),
            new Position(3081, 3729, 0),
            new Position(2349, 3052, 0),
            new Position(2347, 3053, 0),
            new Position(2347, 3055, 0),
            new Position(2350, 3056, 0),
            new Position(2348, 3058, 0),
            new Position(2352, 3055, 0),
            new Position(2353, 3053, 0),
            new Position(2350, 3051, 0),
            new Position(2352, 3050, 0),
            new Position(2352, 3052, 0),
            new Position(2347, 3051, 0),
            new Position(2345, 3053, 0),
            new Position(2345, 3056, 0),
            new Position(2347, 3056, 0),
            new Position(2376, 3057, 0),
            new Position(2376, 3054, 0),
            new Position(2377, 3052, 0),
            new Position(2379, 3052, 0),
            new Position(2381, 3053, 0),
            new Position(2381, 3056, 0),
            new Position(2380, 3057, 0),
            new Position(2378, 3058, 0),
            new Position(2407, 3055, 0),
            new Position(2406, 3054, 0),
            new Position(2406, 3052, 0),
            new Position(2408, 3051, 0),
            new Position(2410, 3051, 0),
            new Position(2411, 3052, 0),
            new Position(2410, 3054, 0),
            new Position(2410, 3055, 0),
            new Position(2408, 3055, 0),
            new Position(2420, 3053, 0),
            new Position(2420, 3051, 0),
            new Position(2422, 3050, 0),
            new Position(2424, 3050, 0),
            new Position(2426, 3050, 0),
            new Position(2426, 3052, 0),
            new Position(2425, 3054, 0),
            new Position(2423, 3055, 0),
            new Position(2421, 3055, 0),
            new Position(2437, 3058, 0),
            new Position(2436, 3056, 0),
            new Position(2436, 3054, 0),
            new Position(2438, 3053, 0),
            new Position(2440, 3053, 0),
            new Position(2442, 3054, 0),
            new Position(2443, 3056, 0),
            new Position(2441, 3057, 0),
            new Position(2439, 3059, 0),
            new Position(2469, 3134, 0),
            new Position(2469, 3131, 0),
            new Position(2471, 3130, 0),
            new Position(2475, 3130, 0),
            new Position(2477, 3131, 0),
            new Position(2477, 3134, 0),
            new Position(2474, 3137, 0),
            new Position(2471, 3137, 0),
            new Position(2469, 3135, 0),
            new Position(2497, 3192, 0),
            new Position(2497, 3195, 0),
            new Position(2502, 3195, 0),
            new Position(2503, 3194, 0),
            new Position(2502, 3191, 0),
            new Position(2499, 3190, 0),
            new Position(2499, 3195, 0),
            new Position(2571, 3187, 0),
            new Position(2570, 3186, 0),
            new Position(2570, 3183, 0),
            new Position(2572, 3183, 0),
            new Position(2576, 3185, 0),
            new Position(2575, 3186, 0),
            new Position(2574, 3188, 0),
            new Position(2572, 3188, 0),
            new Position(2574, 3183, 0),
            new Position(2781, 3463, 0),
            new Position(2785, 3465, 0),
            new Position(2729, 3436, 0),
            new Position(2730, 3442, 0),
            new Position(2732, 3436, 0),
            new Position(3494, 3218, 0),
            new Position(3495, 3220, 0),
            new Position(3498, 3219, 0),
            new Position(3501, 3219, 0),
            new Position(3495, 3222, 0),
            new Position(3493, 3218, 0),
            new Position(3490, 3218, 0),
            new Position(3488, 3215, 0),
            new Position(3483, 3215, 0),
            new Position(3483, 3219, 0),
            new Position(3484, 3220, 0),
            new Position(3502, 3215, 0),
            new Position(3504, 3218, 0),
            new Position(3509, 3219, 0),
            new Position(3510, 3216, 0),
            new Position(3508, 3213, 0),
            new Position(3504, 3214, 0),
            new Position(3661, 3489, 0),
            new Position(3661, 3499, 0),
            new Position(3647, 3532, 0),
            new Position(3452, 3511, 0),
            new Position(3300, 2787, 0),
            new Position(3304, 2787, 0),
            new Position(2750, 3536, 0),
            new Position(2725, 3532, 0)
    };
}