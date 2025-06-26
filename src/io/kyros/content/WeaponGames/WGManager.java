package io.kyros.content.WeaponGames;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.QuestTab;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.seasons.Halloween;
import io.kyros.content.tournaments.ViewingOrb;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.content.worldevent.impl.WGWorldEvent;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Coordinate;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.util.Misc;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WGManager {

    private static volatile WGManager singleton;

    private final ArrayList<String> currentPlayers = new ArrayList<>();

    private final HashMap<Player, Integer> KillTracker = new HashMap<>();

    private static final int LOBBY_TICK_INTERVAL = 1;

    public static HashMap<Player, Integer> killList = new HashMap<>();

    public static long startTimer = 0;

    public long powerupTimer = 0;

    public static Player thirdPlayer = null;

    public static Player secondPlayer = null;

    public static Player firstPlayer = null;

    public static long mediPack = 0;

    public static boolean active = false;

    public static List<Map.Entry<Player, Integer>> getTopKeysWithOccurences(Map mp, int top) {
        List<Map.Entry<Player,Integer>> results = new ArrayList<>(mp.entrySet());
        Collections.sort(results, (e1,e2) -> e2.getValue() - e1.getValue());
        if (results.size() < top) {
            top = results.size();
        }
        return results.subList(0, top);
    }

    private static int _secondsUntilLobbyEnds;
    private static int _playersToStart;

    private final Object wGtimerObject = new Object();

    private static final Object WG_TASK_OBJECT = new Object();

    /**
    * Weapons for the game, must follow the order of upgrade as per kill ect.
    */

    private final int[] weapons = {1321, 1323, 1325, 1327, 1329, 1331, 1333};
    private boolean lobbyOpen;
    private boolean arenaActive;

    private int secondsUntilLobbyEnds = _secondsUntilLobbyEnds;

    public static void initialiseSingleton() {
        if (!Server.isDebug()) {
            _secondsUntilLobbyEnds = 60;
            _playersToStart = 2;
        } else {
            _secondsUntilLobbyEnds = 10;
            _playersToStart = 1;
        }
        singleton = new WGManager();
    }

    public static WGManager getSingleton() {
        return singleton;
    }

    private WGManager() {
        if (singleton != null) {
            throw new RuntimeException("Use getSingleton() method to get the single instance of this class.");
        }
    }

    public void openLobby() {
        if (!killList.isEmpty()) {
            killList.clear();
        }
        initializeLobbyTimer();
    }

    private void initializeLobbyTimer() {
        // Initalize the variables to control the lobby
        clearActiveLobby();
        CycleEventHandler.getSingleton().stopEvents(wGtimerObject);
        secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
        lobbyOpen = true;
        QuestTab.updateAllQuestTabs();
        // Start lobby timer
        CycleEventHandler.getSingleton().addEvent(wGtimerObject, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int remaining = secondsUntilLobbyEnds < 1 ? 0 : (secondsUntilLobbyEnds -= LOBBY_TICK_INTERVAL);
                if (currentPlayers.size() < _playersToStart) {
                    secondsUntilLobbyEnds = _secondsUntilLobbyEnds;
                } else if (remaining <= 0) {
                    beginWeaponGames();
                    container.stop();
                }
                updateInterfaceX();
                QuestTab.updateAllQuestTabs();
            }
        }, 2);
    }

    private void clearActiveLobby() {
        if (isLobbyOpen()) {
            lobbyOpen = false;
            ArrayList<Player> toLeave = Lists.newArrayList();
            for (String name : currentPlayers) {
                Player p = PlayerHandler.getPlayerByLoginName(name);
                if (p != null) {
                    toLeave.add(p);
                }
            }
            for (Player p : toLeave) {
                if (p != null) {
                    leaveLobby(p, false);
                }
            }
            toLeave.clear();
            currentPlayers.clear();
        }
    }


    public boolean isInLobbyBoundsOnLogin(Player player) {
        return Boundary.isIn(player, new Boundary(1886,4258,1895,4264));
    }

    public boolean isInArenaBoundsOnLogin(Player player) {
        return Boundary.isIn(player, new Boundary(1880,4241,1901,4258));
    }

    public boolean isInLobbyBounds(Player player) {
        return Boundary.isIn(player, new Boundary(1886,4258,1895,4264));
    }

    /**
     * Checks if a player is standing in the arena
     *
     * @param player
     * @return
     */
    public boolean isInArenaBounds(Player player) {
        return Boundary.isIn(player, new Boundary(1880,4241,1901,4258));
    }

    private void beginWeaponGames() {
        ArrayList<String> toRemove = Lists.newArrayList();

        for (String p : currentPlayers) {
            Player player = PlayerHandler.getPlayerByLoginName(p);
            if (player == null) {
                toRemove.add(p);
                continue;
            }
            if (!isInLobbyBounds(player)) {
                player.sendMessage("You will no longer compete in WeaponGames, as you were");
                player.sendMessage("not in the lobby when it began.");
                toRemove.add(p);
                continue;
            }
            if (player.hasOverloadBoost) {
                player.getPotions().resetOverload();
            }

            player.sendMessage("Welcome to the WeaponGames. Goodluck!");
//            player.getPA().movePlayer(map.locations[Misc.random(map.locations.length-1)]);

            player.getPA().movePlayer(Misc.random(1881, 1900), Misc.random(4247, 4252),0);

        }

        for (String r : toRemove) {
            PlayerHandler.getOptionalPlayerByLoginName(r).ifPresent(plr -> leaveLobby(plr, false));
            currentPlayers.remove(r);
        }

        arenaActive = true;
        lobbyOpen = false;
        QuestTab.updateAllQuestTabs();
        // End arena tasks
        CycleEventHandler.getSingleton().stopEvents(WG_TASK_OBJECT);
        CycleEventHandler.getSingleton().addEvent(WG_TASK_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (currentPlayers.isEmpty() || Boundary.getPlayersInBoundary(new Boundary(1856, 4224, 1919, 4287)) == 0) {
                    endGame();
                    container.stop();
                }
            }
        }, 10);

        CycleEventHandler.getSingleton().addEvent(WG_TASK_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {


                if (powerupTimer < System.currentTimeMillis()) {
                    WGPowerup.Start();
                    announceToLobby("PowerUP's have spawned!");
                    powerupTimer = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Misc.random(3,5)));
                }

                if (mediPack < System.currentTimeMillis()) {
                    WGMedPack.Start();
                    announceToLobby("Med Packs have spawned!");
                    mediPack = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(Misc.random(3,5)));
                }

                if (currentPlayers.size() < 2) {
                    container.stop();
                }
            }
        }, 1);

        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (active && (startTimer + TimeUnit.MINUTES.toMillis(15)) <= System.currentTimeMillis() || active && Boundary.getPlayersInBoundary(Boundary.WG_Boundary) <= 1) {
                    handleVictors();
                    container.stop();
                }
            }
        },1);


        for (String name : currentPlayers) {
            Player player = PlayerHandler.getPlayerByLoginName(name);
            if (player != null) {
                announceToLobby("WeaponGames has begun! FIGHT!");
                player.getItems().equipItem(WGModes.SCIMITARS_AND_WHIPS[0], 1,Player.playerWeapon);
                startTimer = System.currentTimeMillis();
                player.canAttack = true;
                active = true;
            }
        }
    }

    public void announceToLobby(String message) {
        for (String currentPlayer : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(currentPlayer);
            if (p != null) {
                p.sendMessage("[<col=ff7000>WG</col>]: " + message);
            }
        }
    }

    public void announce(String message) {
        Server.getPlayers().forEach(p -> {
            if (p != null) {
                p.sendMessage("[<col=ff7000>WG</col>]: " + message);
            }
        });
    }

    public void join(Player player) {
/*        if (player.hasFollower) {
            PetHandler.pickupPet(player, PetHandler.forItem(player.petSummonId).npcId, true);
        }*/
        if (!WGManager.getSingleton().isLobbyOpen()) {
            player.sendMessage("The WeaponGames lobby is not currently open, begin's in " + WGManager.getSingleton().getTimeRemaining());
            return;
        }
        if (isPlayerInTournament(player)) {
            player.sendMessage("You are already a part of WeaponGames");
            return;
        }
        if (isArenaActive()) {
            player.sendMessage("You are unable to join an active WeaponGame lobby.");
            return;
        }
/*        if (checkMacAddress(player) && !Server.isDebug()) {
            player.sendMessage("You can only play with one account per computer.");
            return;//TODO Undo this method after testing!
        }*/
        if (player.getPotions().hasPotionBoost()) {
            player.getPotions().resetPotionBoost();
        }


        player.setRunEnergy(100, true);
        player.playerLevel[5] = player.getPA().getLevelForXP(player.playerXP[5]);
        player.getHealth().removeAllStatuses();
        player.getHealth().reset();
        player.getPA().refreshSkill(5);
        player.getCombatPrayer().resetPrayers();
        player.spectatingTournament = false;
        player.resetVengeance();
        player.specRestore = 120;
        player.specAmount = 10.0;
        player.canAttack = false;
        player.getItems().addSpecialBar(player.playerEquipment[Player.playerWeapon]);
        currentPlayers.add(player.getLoginName());
        player.getPA().movePlayer(Misc.random(1887, 1894), Misc.random(4259, 4263), 0);
        player.sendMessage("You have joined the WG lobby.");
        player.sendMessage("Remain in this lobby until the WeaponGames begins.");

        player.setSpellId(-1);
        for (String currentPlayer : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(currentPlayer);
            if (p != null) {
                updateInterface(p);
            }
        }
    }

    /**
     * Checks a players mac address against all in the current lobby
     */
    public boolean checkMacAddress(Player player) {
        for (String playerName : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(playerName);
            if (p != null) {
                if (p.getMacAddress().equalsIgnoreCase(player.getMacAddress())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void leaveLobby(Player player, boolean xlog) {
        if (!player.spectatingTournament && isParticipant(player)) {
            currentPlayers.remove(player.getLoginName());
            player.sendMessage("You have left the WeaponGames lobby.");

            if (xlog)
                player.getPA().forceMove(3092, 3501, 0, true);
            else
                player.getPA().movePlayer(new Coordinate(3092, 3501));
            player.getPA().sendFog(false, 0);
        } else {
            player.setInvisible(false);
            player.getPA().requestUpdates();
            player.getItems().deleteAllItems();
            player.getItems().deleteEquipment();
            player.setTeleportToX(3077);
            player.setTeleportToY(3491);
            player.heightLevel = 0;
            PlayerSave.saveGame(player);
        }
        updateInterface(player);
    }

    /**
     * Handles a player that logs in within the lobby bounds
     */
    public void handleLoginWithinLobby(Player player) {
        getSingleton().leaveLobby(player, false);
    }

    /**
     * Handles a player that logs in within the arena bounds
     */
    public void handleLoginWithinArena(Player player) {
        if (!player.spectatingTournament) {
            player.getCombatPrayer().resetPrayers();
            player.playerWalkIndex = 819;
            player.playerStandIndex = 808;
            player.playerRunIndex = 824;
        } else {
            ViewingOrb.leaveSpectatorTournament(player);
        }
    }
    public boolean isInArena(Player player) {
        if (!isParticipant(player)) {
            return false;
        }
        return isArenaActive() && isInArenaBounds(player);
    }
    /**
     * Handles the killer of a player getting rewarded
     */
    public void handleKill(Player attacker, Player defender) {
        if (attacker == null) {
            return;
        }
        if (defender == null) {
            return;
        }
        if (currentPlayers.size() == 0 || !isArenaActive()) {
            return;
        }

        if (killList.containsKey(attacker)) {
            killList.put(attacker, killList.get(attacker) + 1);
        } else {
            killList.put(attacker, 1);
        }
        attacker.WGKIlls++;
        attacker.attacking.reset();

        for (String currentPlayer : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(currentPlayer);
            if (p != null) {
                updateInterface(p);
            }
        }

        if (killList.get(attacker) >= 30) {
            handleVictors();
        }

        int nextWep = 0;
        for (int i = 0; i < WGModes.SCIMITARS_AND_WHIPS.length; i++) {
            if (WGModes.SCIMITARS_AND_WHIPS[i] == attacker.playerEquipment[Player.playerWeapon] && i != WGModes.SCIMITARS_AND_WHIPS_MAX) {
                nextWep = (i+1);
                if (nextWep == 12) {
                    announceToLobby(attacker.getDisplayName() + " has reached the final weapon!");
                }
                break;
            } else if (i == WGModes.SCIMITARS_AND_WHIPS_MAX) {
                nextWep = WGModes.SCIMITARS_AND_WHIPS_MAX;
            }
        }

        attacker.getItems().equipItem(WGModes.SCIMITARS_AND_WHIPS[nextWep], 1, Player.playerWeapon);

        //TODO Add Upgrade weapons.
    }


    public void handleDeath(String playerName, boolean logout) {
        if (!currentPlayers.contains(playerName)) {
            System.err.println("containers players name??? " + playerName);
            return;
        }

        Player player = PlayerHandler.getPlayerByLoginName(playerName);
        if (player != null) {
            if (logout) {
                currentPlayers.remove(player.getLoginName());
                player.attacking.reset();
                player.getPA().forceMove(3092, 3501, 0, true);
//                System.out.println("Player logged out!");
                return;
            }
            player.attacking.reset();
            //TODO Add WeaponGames Leaderboard
            if (player.playerEquipment[Player.playerWeapon] != 1321 && player.playerEquipment[Player.playerWeapon] != -1) {
                int prevWep = 0;
                for (int i = 0; i < WGModes.SCIMITARS_AND_WHIPS.length; i++) {
                    if (WGModes.SCIMITARS_AND_WHIPS[i] == player.playerEquipment[Player.playerWeapon] && i != WGModes.SCIMITARS_AND_WHIPS_MAX) {
                        prevWep = (i-1);
                        break;
                    } else if (i == WGModes.SCIMITARS_AND_WHIPS_MAX) {
                        prevWep = WGModes.SCIMITARS_AND_WHIPS_MAX-1;
                    }
                }
            player.getItems().equipItem(WGModes.SCIMITARS_AND_WHIPS[prevWep], 1, Player.playerWeapon);
            player.sendMessage("@red@You have lost a weapon tier!");
            }
            player.getPA().removePlayerHintIcon();
            player.getPA().movePlayer(Misc.random(1881, 1900), Misc.random(4247, 4252),0);
        }
        for (String currentPlayer : currentPlayers) {
            Player p = PlayerHandler.getPlayerByLoginName(currentPlayer);
            if (p != null) {
                updateInterface(p);
            }
        }
    }
    private void handleVictors() {
        //TODO Handle winners & rewards.
        lobbyOpen = false;
        arenaActive = false;

        if (!killList.isEmpty()) {
            if (killList.size() == 1) {
                firstPlayer = getTopKeysWithOccurences(killList, 1).get(0).getKey();
            }
            if (killList.size() == 2) {
                firstPlayer = getTopKeysWithOccurences(killList, 1).get(0).getKey();
                secondPlayer = getTopKeysWithOccurences(killList, 2).get(1).getKey();
            }
            if (killList.size() >= 3) {
                firstPlayer = getTopKeysWithOccurences(killList, 1).get(0).getKey();
                secondPlayer = getTopKeysWithOccurences(killList, 2).get(1).getKey();
                thirdPlayer = getTopKeysWithOccurences(killList, 3).get(2).getKey();
            }
        }

/*        if (WGWinners.WINNERS.size() > 11) {
            WGWinners.WINNERS.remove(0);
        }//TODO Configure New Leaderboard

        WGWinners winner = new WGWinners(firstPlayer.getPlayerName(), rewards[0].getId(), rewards[0].getAmount());

        winner.setWonAtTime(LocalDateTime.now());
        WGWinners.WINNERS.add(winner);
        WGWinners.saveAllWinners();*/

        if (firstPlayer != null) {
            announce("[<col=ff7000>WG</col>]: "
                    + firstPlayer.getDisplayName() + " has won first place!");
            firstPlayer.WGPoints += currentPlayers.size() + 10;

            if (Halloween.TripleWGToruney) {
                firstPlayer.WGPoints += 20;
            }
            firstPlayer.sendMessage("@blu@Your total point's are now: " + firstPlayer.WGPoints + ".");
        }

        if (secondPlayer != null) {
            announce("[<col=ff7000>WG</col>]: "
                    + secondPlayer.getDisplayName() + " has won second place!");
            secondPlayer.WGPoints += currentPlayers.size() + 5;
            if (Halloween.TripleWGToruney) {
                secondPlayer.WGPoints += 15;
            }
            secondPlayer.sendMessage("@blu@Your total point's are now: " + secondPlayer.WGPoints + ".");
        }

        if (thirdPlayer != null) {
            announce("[<col=ff7000>WG</col>]: "
                    + thirdPlayer.getDisplayName() + " has won third place!");
            thirdPlayer.WGPoints += currentPlayers.size() + 1;
            if (Halloween.TripleWGToruney) {
                thirdPlayer.WGPoints += 10;
            }
            thirdPlayer.sendMessage("@blu@Your total point's are now: " + thirdPlayer.WGPoints + ".");
        }

        endGame();
    }

    public void handleLogout(Player player) {
        if (!isParticipant(player))
            return;
        if (isArenaActive()) {
            handleDeath(player.getLoginName(), true);
        } else {
            leaveLobby(player, true);
        }
    }

    private String getLobbyTime() {
        int minutes = secondsUntilLobbyEnds / 60;
        int seconds = secondsUntilLobbyEnds % 60;

        if (secondsUntilLobbyEnds == 0)
            return "Moving to game..";
        return "Starts in: "+String.format("%d:%02d", minutes, seconds);
    }

    public int getPlayers() {
        return currentPlayers.size();
    }


    public String getTimeLeft() {
        if (isArenaActive()) {
            return "WG: @gre@Active";
        } else if (isLobbyOpen()) {
            return "WG: @whi@" + getLobbyTime();
        } else {
            return "WG: @red@" + WorldEventContainer.getInstance().getTimeUntilEvent(new WGWorldEvent()) + " minutes";
        }
    }

    public String getTimeRemaining() {
        if (isArenaActive()) {
            return "Active";
        } else if (isLobbyOpen()) {
            return getLobbyTime();
        } else {
            return WorldEventContainer.getInstance().getTimeUntilEvent(new WGWorldEvent()) + " minutes";
        }
    }

    public boolean isLobbyOpen() {
        return lobbyOpen;
    }

    public boolean isArenaActive() {
        return arenaActive;
    }

    private List<Player> getActivePlayers() {
        return currentPlayers.stream().map(PlayerHandler::getPlayerByLoginName).filter(Objects::nonNull).collect(Collectors.toList());
    }


    private boolean isPlayerInTournament(Player player) {
        return currentPlayers.contains(player.getLoginName());
    }

    public boolean isParticipant(Player player) {
        return currentPlayers.contains(player.getLoginName());
    }

    public void endGame() {
        getActivePlayers().forEach(it -> leaveLobby(it, false));
        killList.clear();
        currentPlayers.clear();
        arenaActive = false;
        startTimer = 0;
        active = false;
        CycleEventHandler.getSingleton().stopEvents(WG_TASK_OBJECT);
        lobbyOpen = false;
    }

    private void updateInterfaceX() {
        currentPlayers.forEach(p -> {
            Player player = PlayerHandler.getPlayerByLoginName(p);
            if (player != null) {
                updateInterface(player);
            }
        });
    }

    public static void updateInterface(Player player) {
        if (killList.isEmpty()) {
            player.getPA().sendString(25986," ");
            player.getPA().sendString(25987," ");
            player.getPA().sendString(25988," ");

            player.getPA().sendString(25992," ");
            player.getPA().sendString(25993," ");
            player.getPA().sendString(25994," ");
        }

        if (killList.size() == 1) {
            Player first = getTopKeysWithOccurences(killList, 1).get(0).getKey();
            player.getPA().sendString(25986, StringUtils.abbreviate(first.getDisplayName(), 7));
            player.getPA().sendString(25987," ");
            player.getPA().sendString(25988," ");

            player.getPA().sendString(25992, String.valueOf(killList.get(first)));
            player.getPA().sendString(25993," ");
            player.getPA().sendString(25994," ");
        }
        if (killList.size() == 2) {
            Player first = getTopKeysWithOccurences(killList, 2).get(0).getKey();
            Player second = getTopKeysWithOccurences(killList, 2).get(1).getKey();
            player.getPA().sendString(25986, StringUtils.abbreviate(first.getDisplayName(), 7));
            player.getPA().sendString(25987,StringUtils.abbreviate(second.getDisplayName(), 7));
            player.getPA().sendString(25988," ");

            player.getPA().sendString(25992, String.valueOf(killList.get(first)));
            player.getPA().sendString(25993,String.valueOf(killList.get(second)));
            player.getPA().sendString(25994," ");
        }
        if (killList.size() >= 3) {
            Player first = getTopKeysWithOccurences(killList, 3).get(0).getKey();
            Player second = getTopKeysWithOccurences(killList, 3).get(1).getKey();
            Player third = getTopKeysWithOccurences(killList, 3).get(2).getKey();
            player.getPA().sendString(25986, StringUtils.abbreviate(first.getDisplayName(), 7));
            player.getPA().sendString(25987,StringUtils.abbreviate(second.getDisplayName(), 7));
            player.getPA().sendString(25988,StringUtils.abbreviate(third.getDisplayName(), 7));

            player.getPA().sendString(25992, String.valueOf(killList.get(first)));
            player.getPA().sendString(25993,String.valueOf(killList.get(second)));
            player.getPA().sendString(25994,String.valueOf(killList.get(third)));
        }
    }
}
