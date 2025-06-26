package io.kyros.content.minigames.castlewars;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.combat.melee.MeleeData;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.ContainerUpdate;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.HashMap;
import java.util.Iterator;

public class CastleWarsLobby {

    // Game timers
    public static final int GAME_TIMER = 200; // 15 minutes (1500 * 600 = 900000ms)
    private static final int GAME_START_TIMER = 30;

    // Player storage for waiting room and game room
    private static final HashMap<Player, Integer> waitingRoom = new HashMap<>();
    private static final HashMap<Player, Integer> gameRoom = new HashMap<>();

    // Coordinates for waiting and game rooms (Saradomin and Zamorak)
    private static final int[][] WAIT_ROOM = {{2377, 9485}, {2421, 9524}};
    private static final int[][] GAME_ROOM = {{2426, 3076}, {2372, 3131}};
    private static final int[][] FLAG_STANDS = {{2429, 3074}, {2370, 3133}};

    // Game status variables
    public static boolean[] PICKLOCK_STATUS = {false, false};
    public static int[] scores = {0, 0};
    public static int[] rocks = {100, 100, 100, 100};
    private static final int[][] rocksLocations = {{2409, 9503}, {2401, 9494}};
    private static int zammyFlag = 0;
    private static int saraFlag = 0;
    public static final int SARA_BANNER = 4037;
    public static final int ZAMMY_BANNER = 4039;
    public static final int SARA_CAPE = 4514;
    public static final int ZAMMY_CAPE = 4516;
    public static final int SARA_HOOD = 4513;
    public static final int ZAMMY_HOOD = 4515;
    private static int properTimer = 0;
    public static int timeRemaining = -1;
    public static int gameStartTimer = GAME_START_TIMER;
    private static boolean gameStarted = false;
    public static int saraBarricades, zammyBarricades;

    /**
     * Adds a player to the waiting room.
     *
     * @param player The player to add.
     * @param team   The team the player wishes to join.
     */
    public static void addToWaitRoom(Player player, int team) {
        if (player == null) {
            return;
        }

        if (gameStarted) {
            player.sendErrorMessage("Looks like a game is already in progress!!");
            /*if (player.playerEquipment[Player.playerHat] > 0 || player.playerEquipment[Player.playerCape] > 0) {
                player.sendMessage("You may not wear capes or helmets inside Castle Wars.");
                return;
            }

            handleGameInProgress(player);*/
            return;
        }

        if (player.playerEquipment[Player.playerHat] > 0 || player.playerEquipment[Player.playerCape] > 0) {
            player.sendMessage("You may not wear capes or helmets inside Castle Wars.");
            return;
        }

        toWaitingRoom(player, team);
    }

    /**
     * Moves a player to the waiting room.
     *
     * @param player The player to move.
     * @param team   The team the player wishes to join.
     */
    public static void toWaitingRoom(Player player, int team) {
        if (team == 1) {
            if (getSaraPlayers() > getZammyPlayers()) {
                player.sendMessage("The Saradomin team is full, try again later!");
                return;
            }
            addToTeam(player, team, SARA_CAPE, SARA_HOOD, "Saradomin");
        } else if (team == 2) {
            if (getZammyPlayers() > getSaraPlayers()) {
                player.sendMessage("The Zamorak team is full, try again later!");
                return;
            }
            addToTeam(player, team, ZAMMY_CAPE, ZAMMY_HOOD, "Zamorak");
        } else {
            toWaitingRoom(player, getZammyPlayers() > getSaraPlayers() ? 1 : 2);
        }
    }

    private static void addToTeam(Player player, int team, int capeId, int hoodId, String teamName) {
        player.sendMessage("You have been added to the " + teamName + " team.");
        player.sendMessage("Next Game Begins In: " + ((gameStartTimer * 3) + (timeRemaining * 3)) + " seconds.");
        addCapes(player, capeId);
        addHood(player, hoodId);
        waitingRoom.put(player, team);
        player.getPA().movePlayer(WAIT_ROOM[team - 1][0] + Misc.random(5), WAIT_ROOM[team - 1][1] + Misc.random(5), 0);
    }

    private static void handleGameInProgress(Player player) {
        int team = (scores[0] > scores[1]) ? 2 : 1;
        int capeId = (team == 2) ? ZAMMY_CAPE : SARA_CAPE;
        addCapes(player, capeId);
        gameRoom.put(player, team);
        moveToGameRoom(player, team);
        updateGameInterface(player);
    }

    private static void moveToGameRoom(Player player, int team) {
        player.getPA().walkableInterface(-1);
        player.getPA().movePlayer(GAME_ROOM[team - 1][0] + Misc.random(3), GAME_ROOM[team - 1][1] - Misc.random(3), 1);
        player.getPA().movePlayer(GAME_ROOM[team - 1][0] + Misc.random(3), GAME_ROOM[team - 1][1] - Misc.random(3), 1);
    }

    private static void updateGameInterface(Player player) {
        player.getPA().walkableInterface(11146);
        player.getPA().sendFrame126("Zamorak = " + scores[1], 11147);
        player.getPA().sendFrame126(scores[0] + " = Saradomin", 11148);
        player.getPA().sendFrame126(timeRemaining * 3 + " secs", 11155);
        player.getPA().sendFrame87(378, 2097152 * saraFlag);
        player.getPA().sendFrame87(377, 2097152 * zammyFlag);
    }

    /**
     * Handles the flag return by a player.
     *
     * @param player   The player who returned the flag.
     * @param wearItem The banner item the player is wearing.
     */
    public static void returnFlag(Player player, int wearItem) {
        if (player == null || (wearItem != SARA_BANNER && wearItem != ZAMMY_BANNER)) {
            return;
        }

        int team = gameRoom.get(player);
        int objectId = -1;
        int	objectTeam	= -1;

        if (team == 1) {
            if (wearItem == SARA_BANNER) {
                objectId = 4902;
                objectTeam = 0;
                handleFlagReturn("Saradomin", 0, 4902, 0, player);
            } else {
                objectId = 4903;
                objectTeam = 1;
                handleFlagReturn("Zamorak", 1, 4903, 1, player);
            }
        } else if (team == 2) {
            if (wearItem == ZAMMY_BANNER) {
                objectId = 4903;
                objectTeam = 1;
                handleFlagReturn("Zamorak", 1, 4903, 1, player);
            } else {
                objectId = 4902;
                objectTeam = 0;
                handleFlagReturn("Saradomin", 0, 4902, 0, player);
            }
        }

        changeFlagObject(objectId, objectTeam);
        player.getPA().createPlayerHints(10, -1);
        player.getItems().deleteEquipment(Player.playerWeapon);
    }

    private static void handleFlagReturn(String teamName, int flagIndex, int objectId, int objectTeam, Player player) {
        if (teamName.equals("Saradomin")) {
            setSaraFlag(0);
        } else {
            setZammyFlag(0);
        }

        scores[flagIndex]++;
        player.sendMessage("The team of " + teamName + " scores 1 point!");
    }

    /**
     * Captures the flag when taken by an enemy player.
     *
     * @param player The player who captured the flag.
     */
    public static void captureFlag(Player player) {
        if (player.getItems().getWeapon() != -1) {
            player.sendMessage("Please remove your weapon before attempting to get the flag again!");
            return;
        }
        if (!player.getItems().hasFreeSlots()) {
            player.sendMessage("You don't have enough space to do this right now.");
            return;
        }

        int team = gameRoom.get(player);
        if (team == 2 && saraFlag == 0) {
            captureSpecificFlag(player, SARA_BANNER, 1, 4377, 0);
        } else if (team == 1 && zammyFlag == 0) {
            captureSpecificFlag(player, ZAMMY_BANNER, 2, 4378, 1);
        }
    }

    private static void captureSpecificFlag(Player player, int flagId, int flagIndex, int objectId, int objectTeam) {
        if (flagIndex == 1) {
            setSaraFlag(1);
        } else {
            setZammyFlag(1);
        }

        addFlag(player, flagId);
        createHintIcon(player, flagIndex);
        changeFlagObject(objectId, objectTeam);
    }

    public static void addFlag(Player player, int flagId) {
        player.getItems().equipItem(flagId, 1, Player.playerWeapon);
    }

    public static void pickupFlag(Player player, int objectId) {
        int flagId = (objectId == 4900) ? SARA_BANNER : ZAMMY_BANNER;
        int teamIndex = (gameRoom.get(player) == 1) ? 2 : 1;

        if (objectId == 4900) {
            setSaraFlag(1);
        } else if (objectId == 4901) {
            setZammyFlag(1);
        }

        addFlag(player, flagId);
        createHintIcon(player, teamIndex);
    }

    public static void createHintIcon(Player player, int teamIndex) {
        for (Player teamPlayer : gameRoom.keySet()) {
            teamPlayer.getPA().createPlayerHints(10, -1);
            if (gameRoom.get(teamPlayer) == teamIndex) {
                teamPlayer.getPA().requestUpdates();
            }
        }
    }

    public static void createFlagHintIcon(int x, int y) {
        for (Player teamPlayer : gameRoom.keySet()) {
            teamPlayer.getPA().createObjectHints(x, y, 170, 2);
        }
    }

    public static int getTeamNumber(Player player) {
        if (waitingRoom.containsKey(player)) {
            return waitingRoom.get(player);
        }
        return gameRoom.getOrDefault(player, -1);
    }

    public static void leaveWaitingRoom(Player player) {
        if (player == null) {
            return;
        }

        if (waitingRoom.containsKey(player)) {
            waitingRoom.remove(player);
            player.getPA().createPlayerHints(10, -1);
            player.sendMessage("You left your team!");
            deleteGameItems(player);
            player.getPA().movePlayer(2439 + Misc.random(4), 3085 + Misc.random(5), 0);
        } else {
            player.getPA().movePlayer(2439 + Misc.random(4), 3085 + Misc.random(5), 0);
        }
    }

    public static void process() {
        if (properTimer > 0) {
            properTimer--;
            return;
        }
        properTimer = 4;

        if (gameStartTimer > 0) {
            gameStartTimer--;
            updatePlayers();
        } else if (gameStartTimer == 0) {
            startGame();
        }

        if (timeRemaining > 0) {
            timeRemaining--;
            updateInGamePlayers();
        } else if (timeRemaining == 0) {
            endGame();
        }
    }

    public static void updatePlayers() {
        for (Player player : waitingRoom.keySet()) {
            if (player != null) {
                player.getPA().sendFrame126("Next Game Begins In: " + ((gameStartTimer * 3) + (timeRemaining * 3)) + " seconds.", 6570);
                player.getPA().sendFrame126("Zamorak Players: " + getZammyPlayers(), 6572);
                player.getPA().sendFrame126("Saradomin Players: " + getSaraPlayers(), 6664);
                player.getPA().walkableInterface(6673);
            }
        }
    }

    public static void updateInGamePlayers() {
        if (getSaraPlayers() > 0 && getZammyPlayers() > 0) {
            for (Player player : gameRoom.keySet()) {
                if (player == null) {
                    continue;
                }

                player.getPA().walkableInterface(11146);
                player.getPA().sendFrame126("Zamorak = " + scores[1], 11147);
                player.getPA().sendFrame126(scores[0] + " = Saradomin", 11148);
                player.getPA().sendFrame126(timeRemaining * 3 + " secs", 11155);
                player.getPA().sendFrame87(378, 2097152 * saraFlag);
                player.getPA().sendFrame87(377, 2097152 * zammyFlag);
            }
        }
    }

    public static void startGame() {
        if (getSaraPlayers() < 1 || getZammyPlayers() < 1) {
            gameStartTimer = GAME_START_TIMER;
            return;
        }

        gameStartTimer = -1;
        gameStarted = true;
        timeRemaining = GAME_TIMER;

        for (Player player : waitingRoom.keySet()) {
            int team = waitingRoom.get(player);
            moveToGameRoom(player, team);
            updateGameInterface(player);
            gameRoom.put(player, team);
        }

        waitingRoom.clear();
    }

    public static void endGame() {
        for (Player player : Server.getPlayers().nonNullStream().toList()) {
            if (gameRoom.containsKey(player)) {
                handleGameEnd(player);
            }
        }
        gameRoom.clear();
        resetGame();
    }

    private static void handleGameEnd(Player player) {
        int team = gameRoom.get(player);
        deleteGameItems(player);
        player.getPA().movePlayer(2440 + Misc.random(3), 3089 - Misc.random(3), 0);
        player.sendMessage("The Castle Wars Game has ended!");
        player.getPA().createPlayerHints(10, -1);

/*        if (scores[0] == scores[1]) {
            player.getItems().addItem(4067, 3);
            player.sendMessage("Tie game! You gain 3 Castle Wars tickets!");
        } else {
            handleTeamEnd(player, team);
        }*/
    }

    private static void handleTeamEnd(Player player, int team) {
        boolean isEventActive = Configuration.CASTLEWARS_EVENT;
        int winTickets = isEventActive ? 10 : 5;
        int loseTickets = isEventActive ? 4 : 2;

        if (team == 1 && scores[0] > scores[1]) {
            player.getItems().addItem(4067, winTickets);
            player.sendMessage("You won the CastleWars Game. You received " + winTickets + " Castle Wars Tickets!" + (isEventActive ? " x2 tickets are active." : ""));
        } else if (team == 2 && scores[1] > scores[0]) {
            player.getItems().addItem(4067, winTickets);
            player.sendMessage("You won the CastleWars Game. You received " + winTickets + " Castle Wars Tickets!" + (isEventActive ? " x2 tickets are active." : ""));
        } else {
            player.getItems().addItem(4067, loseTickets);
            player.sendMessage("You lost the CastleWars Game. You received " + loseTickets + " Castle Wars Tickets!" + (isEventActive ? " x2 tickets are active." : ""));
        }
    }

    public static void resetGame() {
        changeFlagObject(4902, 0);
        changeFlagObject(4903, 1);
        setSaraFlag(0);
        setZammyFlag(0);
        scores[0] = 0;
        scores[1] = 0;
        PICKLOCK_STATUS[0] = false;
        PICKLOCK_STATUS[1] = false;
        saraBarricades = 0;
        zammyBarricades = 0;
        timeRemaining = -1;
        gameStartTimer = GAME_START_TIMER;
        gameStarted = false;
        gameRoom.clear();
    }

    public static void removePlayerFromCw(Player player) {
        if (player == null) {
            return;
        }

        if (gameRoom.containsKey(player)) {
            deleteGameItems(player);
            player.getPA().movePlayer(2440, 3089, 0);
            player.getPA().createPlayerHints(10, -1);
            gameRoom.remove(player);
        }

        if (getZammyPlayers() <= 0 || getSaraPlayers() <= 0) {
            endGame();
        }
    }

    public static void addCapes(Player player, int capeId) {
        player.getItems().equipItem(capeId,1, Player.playerCape);
    }

    public static void addHood(Player player, int hoodId) {
        player.getItems().equipItem(hoodId, 1, Player.playerHat);
    }

    public static void deleteGameItems(Player player) {
        // Remove the banners if the player has them equipped
        if (player.getItems().isWearingItem(SARA_BANNER)) {
            player.getItems().unequipItem(SARA_BANNER, Player.playerWeapon);
        }
        if (player.getItems().isWearingItem(ZAMMY_BANNER)) {
            player.getItems().unequipItem(ZAMMY_BANNER, Player.playerWeapon);
        }

        // Remove the capes if the player has them equipped
        if (player.getItems().isWearingItem(SARA_CAPE)) {
            player.getItems().unequipItem(SARA_CAPE, Player.playerCape);
        }
        if (player.getItems().isWearingItem(ZAMMY_CAPE)) {
            player.getItems().unequipItem(ZAMMY_CAPE, Player.playerCape);
        }
        if (player.getItems().isWearingItem(SARA_HOOD)) {
            player.getItems().unequipItem(SARA_HOOD, Player.playerHat);
        }
        if (player.getItems().isWearingItem(ZAMMY_HOOD)) {
            player.getItems().unequipItem(ZAMMY_HOOD, Player.playerHat);
        }

        // Delete other game-specific items
        int[] items = {4049, 1265, 4045, 4053, 4042, 4041, 4037, 4039,SARA_CAPE,SARA_HOOD,SARA_BANNER,ZAMMY_BANNER,ZAMMY_CAPE,ZAMMY_HOOD};
        for (int item : items) {
            if (player.getItems().playerHasItem(item)) {
                player.getItems().deleteItem2(item, player.getItems().getInventoryCount(item));
            }
        }

        player.getItems().addContainerUpdate(ContainerUpdate.INVENTORY);
    }


    public static int getZammyPlayers() {
        return getPlayersByTeam(2);
    }

    public static int getSaraPlayers() {
        return getPlayersByTeam(1);
    }

    private static int getPlayersByTeam(int team) {
        int players = 0;
        Iterator<Integer> iterator = (!waitingRoom.isEmpty()) ? waitingRoom.values().iterator() : gameRoom.values().iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == team) {
                players++;
            }
        }
        return players;
    }

    public static boolean isInCw(Player player) {
        return gameRoom.containsKey(player);
    }

    public static boolean isInCwWait(Player player) {
        return waitingRoom.containsKey(player);
    }

    public static void setSaraFlag(int status) {
        saraFlag = status;
    }

    public static void setZammyFlag(int status) {
        zammyFlag = status;
    }

    public static void changeFlagObject(int objectId, int team) {
        Server.getGlobalObjects().add(new GlobalObject(objectId, FLAG_STANDS[team][0], FLAG_STANDS[team][1], 3,0,10, -1, -1));

        for (Player player : Server.getPlayers().nonNullStream().toList()) {
            if (player != null && Boundary.isIn(player, Boundary.Castle_Wars)) {
                Server.getGlobalObjects().updateRegionObjects(player);
            }
        }
    }
}
