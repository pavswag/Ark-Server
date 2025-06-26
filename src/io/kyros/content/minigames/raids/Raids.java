package io.kyros.content.minigames.raids;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.item.lootable.other.RaidsChestCommon;
import io.kyros.content.item.lootable.other.RaidsChestRare;
import io.kyros.content.items.LootItem;
import io.kyros.content.items.LootTable;
import io.kyros.content.pet.PetManager;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.model.collisionmap.doors.Location;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static io.kyros.content.minigames.raids.RaidConstants.checkInstances;

public class Raids {

    private static final Logger logger = LoggerFactory.getLogger(Raids.class);

    private static final String RAIDS_DAMAGE_ATTRIBUTE_KEY = "cox_damage";
    private static final int RAIDS_DAMAGE_FOR_REWARD = 550;

    public static int COMMON_KEY = 3456;
    public static int RARE_KEY = 3464;
    public long lastActivity = -1;
    private final Map<String, Long> playerLeftAt = Maps.newConcurrentMap();
    private final Map<String, Integer> raidPlayers = Maps.newConcurrentMap();
    private final Map<String, Integer> activeRoom = Maps.newConcurrentMap();
    private List<RaidsRank> ranks = null;
    private int groupPoints;

    public static boolean isMissingRequirements(Player c) {


        if (c.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) || c.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes))) {

            return c.totalLevel < 1500;
        } else if (c.totalLevel < c.getMode().getTotalLevelNeededForRaids()) {
            c.sendMessage("You need a total level of at least " + c.getMode().getTotalLevelNeededForRaids() + " to join this raid!");
            return true;
        }

        return false;
    }

    public void filterPlayers() {
        raidPlayers.entrySet().stream().filter(entry -> !PlayerHandler.getOptionalPlayerByLoginName(entry.getKey()).isPresent()).forEach(entry -> raidPlayers.remove(entry.getKey()));
    }

    public void removePlayer(Player player) {
        raidPlayers.remove(player.getLoginNameLower());
        groupPoints = raidPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        if (raidPlayers.isEmpty()) {
            lastActivity = System.currentTimeMillis();
        }
    }

    public List<Player> getPlayers() {
        List<Player> activePlayers = Lists.newArrayList();
        filterPlayers();
        raidPlayers.keySet().stream().forEach(playerName -> {
            PlayerHandler.getOptionalPlayerByLoginName(playerName).ifPresent(player -> activePlayers.add(player));
        });
        return activePlayers;
    }

    /**
     * Add points
     */
    public int addPoints(Player player, int points) {
        if (!raidPlayers.containsKey(player.getLoginNameLower())) return 0;
        int currentPoints = raidPlayers.getOrDefault(player.getLoginNameLower(), 0);
        raidPlayers.put(player.getLoginNameLower(), currentPoints + points);
        groupPoints = raidPlayers.entrySet().stream().mapToInt(val -> val.getValue()).sum();
        return currentPoints + points;
    }

    public int currentHeight;
    /**
     * The current path
     */
    private int path;
    /**
     * The current way
     */
    private int way;
    /**
     * Current room
     */
    public int currentRoom;
    private boolean chestRoomDoorOpen = true;
    private final int chestToOpenTheDoor = 5 + Misc.random(20);
    private final HashSet<Integer> chestRoomChestsSearched = new HashSet<>();
//	/**
//	 * Instance;
//	 */
    //private InstancedArea instance = null;
//
    /**
     * Monster spawns (No Double Spawning)
     */
    public boolean lizards;
    public boolean vasa;
    public boolean vanguard;
    public boolean ice;
    public boolean chest;
    public boolean mystic;
    public boolean tekton;
    public boolean mutta;
    public boolean vespula;
    public boolean archers;
    public boolean olm;
    public boolean olmDead;
    public boolean rightHand;
    public boolean leftHand;
    /**
     * The door location of the current paths
     */
    private final ArrayList<Location> roomPaths = new ArrayList<Location>();
    /**
     * The names of the current rooms in path
     */
    private final ArrayList<String> roomNames = new ArrayList<String>();
    /**
     * Current monsters needed to kill
     */
    private int mobAmount;

    /**
     * Gets the start location for the path
     * @return path
     */
    public Location getStartLocation() {
        switch (path) {
            case 0:
                return RaidRooms.STARTING_ROOM.doorLocation;
        }
        return RaidRooms.STARTING_ROOM.doorLocation;
    }

    public Location getOlmWaitLocation() {
        switch (path) {
            case 0:
                return RaidRooms.ENERGY_ROOM.doorLocation;
        }
        return RaidRooms.ENERGY_ROOM.doorLocation;
    }


    /**
     * Handles raid rooms
     * @author Goon
     */
    public enum RaidRooms {
        STARTING_ROOM("start_room", 1, 0, new Location(3299, 5189)),
        LIZARDMEN_SHAMANS("lizardmen", 1, 0, new Location(3308, 5208)),
        VASA_NISTIRIO("vasa", 1, 0, new Location(3312, 5279)),
        VANGUARDS("vanguard", 1, 0, new Location(3312, 5311)),
        ICE_DEMON("ice", 1, 0, new Location(3313, 5346)),
        SKELETAL_MYSTIC("skeletal", 1, 0, new Location(3311, 5374)),
        TEKTON("tekton", 1, 0, new Location(3311, 5403)),
        MUTTADILE("muttadile", 1, 0, new Location(3311, 5434)),
        VESPULA("vespula", 1, 0, new Location(3309, 5466)),
        ENERGY_ROOM("energy", 1, 0, new Location(3313, 5162)),
        OLM_ROOM_WAIT("olm_wait", 1, 0, new Location(3232, 5721)),
        OLM_ROOM("olm", 1, 0, new Location(3232, 5730));

        private final Location doorLocation;
        private final int path;
        private final int way;
        private final String roomName;

        RaidRooms(String name, int path1, int way1, Location door) {
            doorLocation = door;
            roomName = name;
            path = path1;
            way = way1;
        }

        public Location getDoor() {
            return doorLocation;
        }

        public int getPath() {
            return path;
        }

        public int getWay() {
            return way;
        }

        public String getRoomName() {
            return roomName;
        }
    }

    /**
     * Starts the raid.
     */

    GlobalObject crystal;
    public void startRaid(List<Player> players, boolean party) {
        //Initializes the raid
        if (RaidConstants.currentRaidHeight >= 100) {
            RaidConstants.currentRaidHeight = 4;
        }
        crystalTask = new Object();
        currentHeight = RaidConstants.currentRaidHeight;
        RaidConstants.currentRaidHeight += 4;
        crystal = new GlobalObject(30018, 3232,5749, currentHeight);
        Server.getGlobalObjects().add(crystal);
        crystal.getRegionProvider().get(3232,5749).setClipToZero(3232,5749,currentHeight);
        crystal.getRegionProvider().get(3232,5750).setClipToZero(3232,5750,currentHeight);
        crystal.getRegionProvider().get(3233,5750).setClipToZero(3233,5750,currentHeight);
        crystal.getRegionProvider().get(3233,5749).setClipToZero(3233,5749,currentHeight);
        path = 1;
        way = 0;
        for (RaidRooms room : RaidRooms.values()) {
            if (room.getWay() == way) {
                roomNames.add(room.getRoomName());
                roomPaths.add(room.getDoor());
            }
        }
        for (Player lobbyPlayer : players) {
            if (!party) {
                //gets all players in lobby
                if (lobbyPlayer == null) continue;
                if (!lobbyPlayer.getPosition().inRaidLobby()) {
                    lobbyPlayer.sendMessage("You were not in the lobby you have been removed from the raid queue.");
                    continue;
                }
            }
            lobbyPlayer.getPA().closeAllWindows();
            raidPlayers.put(lobbyPlayer.getLoginNameLower(), 0);
            activeRoom.put(lobbyPlayer.getLoginNameLower(), 0);
            lobbyPlayer.setRaidsInstance(this);
            //lobbyPlayer.setInstance(instance);
            lobbyPlayer.getPA().movePlayer(getStartLocation().getX(), getStartLocation().getY(), currentHeight);
            lobbyPlayer.sendMessage("@red@The raid has now started! Good Luck! type ::leaveraids to leave!");
            lobbyPlayer.sendMessage("[TEMP] @blu@If you get stuck in a wall, type ::stuckraids to be sent back to room 1!");
        }
        RaidConstants.raidGames.add(this);

    }

    public boolean hadPlayer(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        return leftAt > 0;
    }

    public boolean login(Player player) {
        long leftAt = playerLeftAt.getOrDefault(player.getLoginNameLower(), (long) -1);
        if (leftAt > 0) {
            playerLeftAt.remove(player.getLoginNameLower());
            if (System.currentTimeMillis() - leftAt <= 60000) {
                raidPlayers.put(player.getLoginNameLower(), 0);
                player.setRaidsInstance(this);
                player.sendMessage("@red@You rejoin the raid!");
                lastActivity = -1;
                return true;
            }
        }
        return false;
    }

    public void logout(Player player) {
        player.setRaidsInstance(null);
        removePlayer(player);
        playerLeftAt.put(player.getLoginNameLower(), System.currentTimeMillis());
        checkInstances();
    }

    public void resetOlmRoom(Player player) {
        this.activeRoom.put(player.getLoginNameLower(), 9);
    }

    public void resetRoom(Player player) {
        this.activeRoom.put(player.getLoginNameLower(), 0);
    }

    /**
     * Kill all spawns for the raid leader if left
     */
    public void killAllSpawns() {
        NPCHandler.kill(currentHeight, currentHeight + 3, 394, 3341, 7563, 7566, 7585, 7560, 7544, 7573, 7604, 7606, 7605, 7559, 7527, 7528, 7529, 7553, 7554, 7555);
    }

    /**
     * Leaves the raid.
     * @param player
     */
    public void leaveGame(Player player) {
        if (System.currentTimeMillis() - player.infernoLeaveTimer < 15000) {
            player.sendMessage("You cannot leave yet, wait a couple of seconds and try again.");
            return;
        }
        player.sendMessage("@red@You have left the Chambers of Xeric.");
        player.getPA().movePlayer(1234, 3567, 0);
        player.setRaidsInstance(null);
        //player.setInstance(null);
        removePlayer(player);
        player.specRestore = 120;
        player.specAmount = 10.0;
        player.setRunEnergy(100, true);
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        player.getPA().refreshSkill(Player.playerPrayer);
        player.getHealth().removeAllStatuses();
        player.getHealth().reset();
        player.getPA().refreshSkill(5);
        checkInstances();

    }

    public static List<RaidsRank> buildRankList(List<RaidsRank> ranks) {
        ranks.sort(Comparator.comparingInt(it -> it.damage));
        ranks = Lists.reverse(ranks);
        for (int index = 0; index < ranks.size(); index++) {
            ranks.get(index).rank = index + 1;
        }

        return ranks;
    }

    final int OLM = 7554;
    final int OLM_RIGHT_HAND = 7553;
    final int OLM_LEFT_HAND = 7555;

    private Object crystalTask = new Object();

    public void handleMobDeath(Player killer, int npcType) {
        checkInstances();
        mobAmount -= 1;
        switch (npcType) {
            case OLM:
                /*
                 * Crystal & Olm removal after olm's death
                 */
                olmDead = true;
                Server.getGlobalObjects().add(new GlobalObject(30028, 3233,5751, currentHeight,0,10));
                getPlayers().forEach(player -> {
                    player.getPA().sendPlayerObjectAnimation(3232, 5749, 7506, 10, 0);
                    player.getPA().sendPlayerObjectAnimation(3220, 5738, 7348, 10, 3);
                    for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
                        if (taskMasterKills.getDesc().contains("Chambers")) {
                            taskMasterKills.incrementAmountKilled(1);
                            player.getTaskMaster().trackActivity(player, taskMasterKills);
                        }
                    }

                    String[] topDamageDealers = getTopDamageDealers();

                    if (topDamageDealers.length > 0) {
                        player.sendMessage("@red@Top Contributors:");

                        for (String pz : topDamageDealers) {
                            player.sendMessage("@pur@"+pz + " : " +raidPlayers.get(pz));
                        }
                    }

                    Achievements.increase(player, AchievementType.COX, 1);

                    player.setLootCox(true);

                    int uniqueBudget = raidPlayers.get(player.getLoginNameLower());
                    for (int i = 0; i < 3; i++) { // up to 3 uniques
                        if (uniqueBudget <= 0)
                            break;
                        int pointsToUse = Math.min(350000, uniqueBudget); // max of 350k points per unique attempt
                        double chance = (double) pointsToUse / 8675 / 100.0; // 1% chance per 4200 points - OSRS = 8675
                        System.out.println("Points = " + pointsToUse + ", Chance = " + chance + ", uniqueBudget = " + uniqueBudget);
                        uniqueBudget -= pointsToUse;
                        if (ThreadLocalRandom.current().nextDouble() < chance) {
                            Player lucker = getPlayerToReceiveUnique(getPlayers());
                            GameItem item = rollUnique();
                            lucker.getRaidRewards().add(item);
                            raidPlayers.remove(lucker.getLoginNameLower());
                            lucker.getCollectionLog().handleDrop(lucker, 7554, item.getId(), item.getAmount(), true);
                        }
                    }
                    //regular drops
                    getPlayers().stream().filter(p -> p.getRaidRewards().isEmpty()).forEach(p -> {
                        int playerPoints = Math.max(131071, raidPlayers.get(player.getLoginNameLower()));
                        if (playerPoints == 0)
                            return;
                        for (int i = 0; i < 2; i++) {
                            GameItem rolled = rollRegular();
                            int amount = rolled.getAmount();
                            if (amount > 1) {
                                amount = Misc.random((amount/2), amount);
                            }

                            if (!rolled.getDef().isStackable() && !rolled.getDef().isNoted())
                                rolled.setId(rolled.getDef().getNotedItemIfAvailable());
                            p.getRaidRewards().add(new GameItem(rolled.getId(), amount));
                        }
                    });
                });

                CycleEventHandler.getSingleton().addEvent(crystalTask, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        crystal.getRegionProvider().get(crystal.getX(), crystal.getY()).removeObject(crystal.getObjectId(), crystal.getX(), crystal.getY(), currentHeight, 10, 0);
                        crystal.setId(-1);
                        Server.getGlobalObjects().add(new GlobalObject(crystal.getObjectId(), crystal.getX(), crystal.getY(), currentHeight,0,10,-1));
                        container.stop();
                    }
                },2);
                return;

            case OLM_RIGHT_HAND:
                rightHand = true;
                if(leftHand == true) {
                    getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable."));
                    Server.getGlobalObjects().add(new GlobalObject(29888, 3220, 5733, currentHeight, 3, 10));
                }else {
                    getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!"));
                }
                //Server.getGlobalObjects().remove(new GlobalObject(29887, 3220, 5733, currentHeight, 3, 10).setInstance(instance));

                //Server.getGlobalObjects().add(new GlobalObject(29888, 3220, 5733, currentHeight, 3, 10).setInstance(instance));
                getPlayers().stream()
                        .forEach(otherPlr -> {
                            otherPlr.getPA().sendPlayerObjectAnimation(3220, 5733, 7352, 10, 3);
                            if(leftHand) {
                                otherPlr.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable.");
                            } else {
                                otherPlr.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!");
                            }
                        });

                return;
            case OLM_LEFT_HAND:
                leftHand = true;
                Server.getGlobalObjects().remove(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
                Server.getGlobalObjects().add(new GlobalObject(29885, 3220, 5743, currentHeight, 3, 10));
                getPlayers().stream()
                        .forEach(otherPlr -> {
                            otherPlr.getPA().sendPlayerObjectAnimation(3220, 5743, 7360, 10, 3);
                            if(rightHand) {
                                otherPlr.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable.");
                            } else {
                                otherPlr.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!");
                            }

                        });
                if(rightHand == true) {
                    Server.getGlobalObjects().remove(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
                    Server.getGlobalObjects().add(new GlobalObject(29885, 3220, 5743, currentHeight, 3, 10));
                    getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated both of The Great Olm's hands he is now vulnerable."));
                }else {
                    getPlayers().stream().forEach(player ->	player.sendMessage("@red@ You have defeated one of The Great Olm's hands destroy the other one quickly!"));
                }
                return;
        }
        if(killer != null) {
            int randomPoints = Misc.random(500,1000) * (3);
            int newPoints = addPoints(killer, randomPoints);

            killer.sendMessage("@red@You receive "+ randomPoints +" points from killing this monster.");
            killer.sendMessage("@red@You now have "+ newPoints +" points.");
        }
        if(mobAmount <= 0) {
            getPlayers().stream().forEach(player ->	player.sendMessage("@red@The room has been cleared and you are free to pass."));
            roomSpawned = false;
        }else {
            getPlayers().stream().forEach(player ->	player.sendMessage("@red@There are "+ mobAmount+" enemies remaining."));
        }
    }

    // Method to get the top damage dealers
    public String[] getTopDamageDealers() {
        // Sort the players by damage in descending order
        Map<String, Integer> sortedPlayers = raidPlayers.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        // Get the top 3 players or fewer if there are less than 3
        int topCount = Math.min(3, sortedPlayers.size());
        String[] topPlayers = new String[topCount];

        int index = 0;
        for (Map.Entry<String, Integer> entry : sortedPlayers.entrySet()) {
            topPlayers[index] = entry.getKey();
            index++;

            if (index >= topCount) {
                break;
            }
        }

        return topPlayers;
    }

    public void spawnRaidsNpc(int npcType, int x, int y, int heightLevel, int WalkingType, int HP, int maxHit, int attack, int defence, boolean attackPlayer) {
        int[] stats = getScaledStats(HP, attack, defence, getPlayers().size());
        attack = stats[0];
        defence = stats[1];
        HP = stats[2];
        NPC npc = NPCSpawning.spawn(npcType, x, y, heightLevel, WalkingType, maxHit, attackPlayer,
                NpcStats.builder().setAttackLevel(attack).setHitpoints(HP).setDefenceLevel(defence).createNpcStats());
        npc.setRaidsInstance(this);
        npc.getBehaviour().setRespawn(false);
        //System.out.println(modifier + " | "  + lowModifier);
    }

    public static int[] getScaledStats(int HP, int attack, int defence, int groupsize) {
        int modifier = 0;
        int attackScale =1;
        int defenceScale =1;
        int baseMod = 100;
        int baseLowMod = 35;
        if (groupsize > 1) {
            if (HP < 200) {
                baseMod = 10;
            }
            modifier = (baseMod + (groupsize * (int) (HP * 0.15))); // groupsize:modifier | 1:1 | 2:1.8 | 3:2.2
            attackScale = (baseLowMod + (groupsize * 10)); // groupsize:modifier | 1:1 | 2:1.2 | 3:1.3
            defenceScale = (baseLowMod + (groupsize * 10)); // groupsize:modifier | 1:1 | 2:1.2 | 3:1.3
        }
        defence = (defence + defenceScale);
        HP = (HP + modifier);
        attack = (attack + attackScale);

        return new int[] { attack, defence, HP };
    }

    public static final int LIZARDMAN_HP = 150;
    public static final int LIZARDMAN_ATTACK = 200;
    public static final int LIZARDMAN_DEFENCE = 100;

    public static final int VASA_HP = 400;
    public static final int VASA_ATTACK = 250;
    public static final int VASA_DEFENCE = 130;

    public static final int OLM_HP = 500;
    public static final int OLM_ATTACK = 272;
    public static final int OLM_DEFENCE = 350;

    /**
     * Spawns npc for the current room
     * @param currentRoom The room
     */
    public void spawnNpcs(int currentRoom) {
        int height = currentHeight;
        System.out.println("Raid height = " + height);
        switch(roomNames.get(currentRoom)) {
            case "lizardmen":
                if(lizards) {
                    return;
                }
                spawnRaidsNpc(7573, 3309, 5261, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
                spawnRaidsNpc(7573, 3310, 5267, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);
                spawnRaidsNpc(7573, 3314, 5264, height, 1, LIZARDMAN_HP, 25, LIZARDMAN_ATTACK, LIZARDMAN_DEFENCE,true);

                lizards = true;
                mobAmount+=3;
                break;
            case "vasa":
                if(vasa) {
                    return;
                }

                spawnRaidsNpc(7566, 3311, 5295, height, -1, VASA_HP, 25, VASA_ATTACK, VASA_DEFENCE,true);

                vasa = true;
                mobAmount+=1;
                break;
            case "vanguard":
                if(vanguard) {
                    return;
                }

                spawnRaidsNpc(7527, 3310, 5325, height, -1, 170, 25, 140, 120,true);// melee vanguard
                spawnRaidsNpc(7528, 3311, 5331, height, -1, 170, 25, 140, 120,true); // range vanguard
                spawnRaidsNpc(7529, 3315, 5328, height, -1, 170, 25, 140, 120,true); // magic vanguard

                vanguard = true;
                mobAmount+=3;
                break;
            case "ice":
                if(ice) {
                    return;
                }

                spawnRaidsNpc(7585, 3310,5368, height, -1, 500, 45, 350, 160,true);

                ice = true;
                mobAmount+=1;
                break;
            case "skeletal":
                if(mystic) {
                    return;
                }
                spawnRaidsNpc(7604, 3304,5387, height, -1, 150, 25, 400, 150,true);
                spawnRaidsNpc(7605, 3310,5383, height, -1, 150, 25, 500, 150,true);
                spawnRaidsNpc(7606, 3318,5387, height, -1, 150, 25, 400, 150,true);
                mobAmount+=3;
                mystic = true;
                break;
            case "tekton":
                if(tekton) {
                    return;
                }
                spawnRaidsNpc(7544, 3312,5419, height, -1, 550, 45, 450, 230,true);
                mobAmount+=1;
                tekton = true;
                break;
            case "muttadile":
                if(mutta) {
                    return;
                }
                spawnRaidsNpc(7563, 3309,5455, height, 1, 300, 25, 400, 220,true);
                mobAmount+=1;
                mutta = true;
                break;
            case "vespula":
                if(vespula) {
                    return;
                }
                spawnRaidsNpc(7538, 3306, 5483, height, 1, 500, 25, 350, 220,true);
                spawnRaidsNpc(7538, 3306, 5489, height, 1, 500, 25, 350, 220,true);
                spawnRaidsNpc(7531, 3305, 5486, height, 1, 1200, 40, 450, 220,true);
                mobAmount+=3;
                vespula = true;
                break;
            case "olm":
                if(olm) {
                    return;
                }

                // TODO custom region object clipping for instances like this
                Server.getGlobalObjects().add(new GlobalObject(29884, 3220, 5743, currentHeight, 3, 10));
                Server.getGlobalObjects().add(new GlobalObject(29887, 3220, 5733, currentHeight, 3, 10));
                Server.getGlobalObjects().add(new GlobalObject(29881, 3220, 5738, currentHeight, 3, 10));
                getPlayers().stream()
                        .forEach(otherPlr -> {
                            otherPlr.getPA().sendPlayerObjectAnimation(3220, 5733, 7350, 10, 3);
                            otherPlr.getPA().sendPlayerObjectAnimation(3220, 5743, 7354, 10, 3);
                            otherPlr.getPA().sendPlayerObjectAnimation(3220, 5738, 7335, 10, 3);
                        });
                spawnRaidsNpc(7553, 3223, 5733, height, -1, 200, 33, 272, 200,false); // left claw
                spawnRaidsNpc(7554, 3223, 5738, height, -1, OLM_HP, 33, OLM_ATTACK, OLM_DEFENCE,true); // olm head
                spawnRaidsNpc(7555, 3223, 5742, height, -1, 200, 33, 272, 200 ,false); // right claw

                olm = true;
                mobAmount+=3;
                break;
            default:
                roomSpawned = false;
                break;
        }

    }
    /**
     * Handles object clicking for raid objects
     * @param player The player
     * @param objectId The object id
     * @param x
     * @param y
     * @return
     */
    public boolean handleObjectClick(Player player, int objectId, int x, int y) {
        player.objectDistance = 3;
        switch(objectId) {
            // Searching chest to
            case 29742:
                if (chestRoomDoorOpen) {
                    player.sendMessage("The room is already opened, no need for more searching.");
                } else {
                    if (chestRoomChestsSearched.contains(Objects.hash(x, y))) {
                        player.sendMessage("This chest has already been searched.");
                    } else {
                        player.startAnimation(6387);
                        chestRoomChestsSearched.add(Objects.hash(x, y));
                        player.sendMessage("You search the chest..");

                        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                if (chestRoomChestsSearched.size() == chestToOpenTheDoor) {
                                    player.sendMessage("You find a lever to open the door..");
                                    player.forcedChat("I found the lever!");
                                    getPlayers().forEach(plr -> plr.sendMessage("@red@The door has been opened."));
                                    chestRoomDoorOpen = true;
                                } else {
                                    player.sendMessage("You find nothing.");
                                    player.startAnimation(65535);
                                }

                                container.stop();
                            }
                        }, 2);
                    }
                }
                return true;
            case 29789://First entrance
            case 29734:
            case 29735:
            case 29879:
                if (roomNames.get(getRoomForPlayer(player)).equalsIgnoreCase("chest") && !chestRoomDoorOpen) {
                    player.sendMessage("This passage way is blocked, you must search the boxes to find the lever to open it.");
                } else {

                    player.objectDistance = 3;
                    nextRoom(player);
                }
                return true;
            case 30066:
                player.objectDistance = 3;
                return true;
            case 29777:
                player.objectDistance = 3;
            case 29778:
                player.objectDistance = 3;
                if(!olmDead) {
                    if(player.objectX == 3298 && player.objectY == 5185) {
                        player.getDH().sendDialogues(10000, -1);
                        return true;
                    }
                    player.sendMessage("You need to complete the raid!");
                    return true;
                }
                if (System.currentTimeMillis() - player.lastMysteryBox < 3000) {
                    return true;
                }
                player.objectDistance = 3;
                player.lastMysteryBox = System.currentTimeMillis();
                player.raidCount += 1;
                resetDamage(player);
                player.healEverything();
                Pass.addExperience(player, 1);
                BossPoints.addManualPoints(player, "chambers of xeric");
                leaveGame(player);
                resetDamage(player);
                PetManager.addXp(player, 150);
                player.tryPetRaidLoot();
                break;

            case 30028:
                player.objectDistance = 3;
                if (player.getLootCox()) {
                    for (int z = 0; z < 6; z++) {
                        player.getPA().itemOnInterface(-1, 1, 22729, z);
                    }

                    player.setLootCox(false);
                    int i = 0;
                    for (GameItem raidReward : player.getRaidRewards()) {
                        int amount = raidReward.getAmount();
                        if (player.daily2xRaidLoot > 0) {
                            amount *= 2;
                        }

                        player.getItems().addItemUnderAnyCircumstance(raidReward.getId(), amount);
                        player.getPA().itemOnInterface(raidReward.getId(), amount, 22729, i++);

                        if (RaidsChestRare.getRareDrops().stream().anyMatch(gameItem -> gameItem.getId() == raidReward.getId())) {
                            PlayerHandler.executeGlobalMessage("@pur@[RARE DROP] " + player.getDisplayName()+ " Has just received @red@" + raidReward.getDef().getName() + " from Chambers of Xeric!");
                        }

                    }

                    if (!player.getRaidRewards().isEmpty()) {
                        if (player.getItems().getInventoryCount(21046) > 0) {
                            player.getItems().deleteItem2(21046, 1);
                            player.sendErrorMessage("You roll a chance at 5 mini CoX Boxes! GL GL");
                            if (Misc.isLucky(5)) {
                                player.getItems().addItemUnderAnyCircumstance(12585,5);
                                player.sendErrorMessage("You lucky mofo, you got 5 Mini CoX Boxes, Gl next time!!");
                            }
                        }
                    }
                }
                player.getRaidRewards().clear();
                player.getPA().showInterface(22725);
                return true;
        }
        return false;
    }

    private Player getPlayerToReceiveUnique(List<Player> players) {
        return Misc.random(players);
    }

    public static GameItem rollRegular() {
        return RaidsChestCommon.randomChestRewards();
    }

    public static GameItem rollUnique() {
        return RaidsChestRare.randomChestRewards();
    }

    private boolean roomSpawned;

    private int getRoomForPlayer(Player player) {
        return activeRoom.getOrDefault(player.getLoginNameLower(), 0);
    }

    /**
     * Goes to the next room, Handles spawning etc.
     */
    public void nextRoom(Player player) {
        player.objectDistance = 3;
        if(activeRoom.getOrDefault(player.getLoginNameLower(), 0) == currentRoom && mobAmount > 0) {
            player.objectDistance = 3;
            player.sendMessage("You need to defeat the current room before moving on!");
            return;
        }
        if(!roomSpawned) {
            player.objectDistance = 3;
            currentRoom+=1;
            roomSpawned = true;
            spawnNpcs(currentRoom);
        }

        int playerRoom = activeRoom.getOrDefault(player.getLoginNameLower(), 0) + 1;
        if (playerRoom >= roomPaths.size()) {
            player.sendMessage("You can't go this way.");
            return;
        }
        player.getPA().movePlayer(roomPaths.get(playerRoom).getX(),
                roomPaths.get(playerRoom).getY(),
                player.getHeight());
        activeRoom.put(player.getLoginNameLower(), playerRoom);

    }

    public static void damage(Player player, int damage) {
        int current = getDamage(player);
        player.getAttributes().setInt(RAIDS_DAMAGE_ATTRIBUTE_KEY, current + damage);
    }

    public static int getDamage(Player player) {
        return player.getAttributes().getInt(RAIDS_DAMAGE_ATTRIBUTE_KEY, 0);
    }

    private static void resetDamage(Player player) {
        player.getAttributes().removeInt(RAIDS_DAMAGE_ATTRIBUTE_KEY);
    }

    public static class RaidsRank {
        private final Player player;
        private int rank;
        private final int damage;

        RaidsRank(Player player, int damage) {
            this.player = player;
            this.damage = damage;
        }

        @Override
        public String toString() {
            return "RaidsRank{" +
                    "player=" + (player == null ? null : player.getDisplayName()) +
                    ", rank=" + rank +
                    ", damage=" + damage +
                    '}';
        }

        public Player getPlayer() {
            return player;
        }

        public int getRank() {
            return rank;
        }

        public int getDamage() {
            return damage;
        }
    }


    private static LootTable uniqueTable = new LootTable()
            .addTable(1,
                    new LootItem(21034, 1, 20), // dexterous scroll
                    new LootItem(21079, 1, 20), // arcane scroll
                    new LootItem(24466, 1, 4),  // Twisted Horns
                    new LootItem(21000, 1, 3),  // twisted buckler
                    new LootItem(21012, 1, 3),  // dragon hunter crossbow
                    new LootItem(21015, 1, 2),  // dinh's bulwark
                    new LootItem(21018, 1, 2),  // ancestral hat
                    new LootItem(21021, 1, 2),  // ancestral top
                    new LootItem(21024, 1, 2),  // ancestral bottom
                    new LootItem(13652, 1,2),   // dragon claws
                    new LootItem(21003, 1,1),   // elder maul
                    new LootItem(21043, 1, 1),  // kodai insignia
                    new LootItem(20997, 1, 1),  // twisted bow
                    new LootItem(25910, 1, 2)   // twisted horn
            );

    private static LootTable regularTable = new LootTable() // regular table. the "amount" here is the number used to determine the amount given to players based on how many points they have, for example 1 soul rune per 20 points
            .addTable(1,
                    new LootItem(560, 20, 1),       // death rune
                    new LootItem(565, 16, 1),       // blood rune
                    new LootItem(566, 10, 1),       // soul rune
                    new LootItem(892, 7, 1),        // rune arrow
                    new LootItem(11212, 70, 1),     // dragon arrow

                    new LootItem(3050, 185, 1),     // grimy toadflax
                    new LootItem(208, 400, 1),      // grimy ranarr weed
                    new LootItem(210, 98, 1),       // grimy irit
                    new LootItem(212, 185, 1),      // grimy avantoe
                    new LootItem(214, 205, 1),      // grimy kwuarm
                    new LootItem(3052, 500, 1),     // grimy snapdragon
                    new LootItem(216, 200, 1),      // grimy cadantine
                    new LootItem(2486, 150, 1),     // grimy lantadyme
                    new LootItem(218, 106, 1),      // grimy dwarf weed
                    new LootItem(220, 428, 1),      // grimy torstol

                    new LootItem(7937, 200, 100),   // pure essence
                    new LootItem(443, 20, 1),       // silver ore
                    new LootItem(454, 20, 1),       // coal
                    new LootItem(445, 45, 1),       // gold ore
                    new LootItem(448, 45, 1),       // mithril ore
                    new LootItem(450, 100, 1),      // adamantite ore
                    new LootItem(452, 750, 1),      // runite ore

                    new LootItem(1624, 100, 1),     // uncut sapphire
                    new LootItem(1622, 170, 1),     // uncut emerald
                    new LootItem(1620, 125, 1),     // uncut ruby
                    new LootItem(1618, 260, 1),     // uncut diamond

                    new LootItem(8781, 100, 10),    // teak plank
                    new LootItem(8783, 240, 10),    // mahogany plank

                    new LootItem(21047, 131071, 1), // torn prayer scroll
                    new LootItem(21027, 131071, 1)  // dark relic
            );
}
