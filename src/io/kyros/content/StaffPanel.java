package io.kyros.content;

import io.kyros.SaveFileReader;
import io.kyros.annotate.PostInit;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerLoad;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.mysql.*;
import io.kyros.util.Misc;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class StaffPanel {
    private final Player player;
    private Player playerCurrentlyViewing = null;

    private OnlineStatusFilter onlineStatusFilter = OnlineStatusFilter.ONLINE;
    private PlayerStatFilter playerStatFilter = PlayerStatFilter.A_TO_Z;

    private List<Player> currentlyViewingPlayers = new LinkedList<>();

    private String currentPlayerNameFilter = "";

    public void open() {
        updatePlayerList(currentPlayerNameFilter);

        player.getPA().showInterface(65100);
    }

    private synchronized void updatePlayerList(String filterName) {
        System.out.println("updating player list for [" + filterName + "]");
        currentlyViewingPlayers.clear();
        File[] files = new File(SaveFileReader.getSaveDirectory()).listFiles();
        if(files == null || files.length == 0)
            return;

        final int[] widgetId = {65156};
        final int[] playersAdded = {0};
        final Map<String, Boolean>[] playersOnline = new Map[]{new LinkedHashMap<>()};
        new Thread() {
            @Override
            public void run() {
                for(File file : files) {
                    String playerName = file.getName().replace(".txt", "");
                    if(filterName.isBlank() || playerName.toLowerCase().contains(filterName.toLowerCase())) {
                        Player newPlayer = PlayerHandler.getPlayerByLoginName(playerName);
                        if (newPlayer != null && onlineStatusFilter != OnlineStatusFilter.OFFLINE) {
                            currentlyViewingPlayers.add(newPlayer);
                            playersOnline[0].put(playerName, true);
                            playersAdded[0]++;
                        } else if (newPlayer == null) {
                            if(onlineStatusFilter == OnlineStatusFilter.ONLINE)
                                continue;
                            newPlayer = new Player(player.getSession());
                            newPlayer.setLoginName(playerName);
                            PlayerLoad.loadGame(newPlayer, playerName, "", true);
                            currentlyViewingPlayers.add(newPlayer);
                            playersOnline[0].put(playerName, false);
                            playersAdded[0]++;
                        }
                    }
                }

                if(playerStatFilter == PlayerStatFilter.A_TO_Z) {
                    currentlyViewingPlayers.sort((p1, p2) -> {
                        String name1 = p1.getLoginName();
                        String name2 = p2.getLoginName();

                        boolean isDigit1 = Character.isDigit(name1.charAt(0));
                        boolean isDigit2 = Character.isDigit(name2.charAt(0));

                        if (isDigit1 && !isDigit2) {
                            return -1;
                        } else if (!isDigit1 && isDigit2) {
                            return 1;
                        } else {
                            return name1.compareToIgnoreCase(name2);
                        }
                    });

                    LinkedHashMap<String, Boolean> sortedPlayersOnline = new LinkedHashMap<>();
                    for (Player player : currentlyViewingPlayers) {
                        String playerName = player.getLoginName();
                        sortedPlayersOnline.put(playerName, playersOnline[0].get(playerName));
                    }
                    playersOnline[0] = sortedPlayersOnline;
                }

                player.queue(() -> {
                    playersOnline[0].forEach((name, online) -> {
                        player.getPA().sendString(widgetId[0], name);
                        player.getPA().sendChangeSprite(widgetId[0] + 1000, online ? (byte) 3 : (byte) 4);
                        widgetId[0]++;
                    });
                    for(int i = widgetId[0]; i <= 66156; i++) {
                        player.getPA().sendString(i, "N/A");
                        player.getPA().sendChangeSprite(i + 1000, (byte) 4);
                    }

                    player.getPA().setScrollableMaxHeight(65150, Math.max(230, playersAdded[0] * 22));
                });
            }
        }.start();
    }

    private void selectPlayer(int index) {
        if(index > currentlyViewingPlayers.size() - 1) {
            player.sendMessage("Could not find player on that button, either wait a few seconds or try expanding your filters.");
            return;
        }
        Player foundPlayer = currentlyViewingPlayers.get(index);
        playerCurrentlyViewing = foundPlayer;
        updatePlayerDetails(foundPlayer);
    }

    private void updatePlayerDetails(Player foundPlayer) {
        player.getPA().sendString(65102, "Staff Panel - " + foundPlayer.getLoginName());
        int skillWidget = 68035;
        for(Skill skill : Skill.values()) {
            player.getPA().sendString(skillWidget++, "<icon=" + Skill.getIconId(skill) + "><col=ff9933>" + foundPlayer.playerLevel[skill.getId()] + "/" + foundPlayer.getLevelForXP(foundPlayer.playerXP[skill.getId()]));
        }

        long milliseconds = (long) foundPlayer.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds - TimeUnit.DAYS.toDays(days));
        String time = days + " days, " + hours + " hrs";
        player.getPA().sendString(68101, "Total Playtime: " + time);
        player.getPA().sendString(68102, "Game Mode: " + foundPlayer.getMode().getType().getFormattedName());
        player.getPA().sendString(68103, "XP Rate: " + foundPlayer.getExpMode().getType().getFormattedName());
        int playerAchievementsCompleted = 0;
        for (Achievements.Achievement value : Achievements.Achievement.values()) {
            if (foundPlayer.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                playerAchievementsCompleted++;
            }
        }
        player.getPA().sendString(68104, "Achievements: " + playerAchievementsCompleted + "/" + Achievements.Achievement.values().length);

        player.sendMessage("Found player [" + foundPlayer.getLoginName() + "], total XP = [" + foundPlayer.getPA().getTotalXP() + "]");
        Misc.executorService.submit(() -> {
            List<String> logs = getLogs(foundPlayer.getLoginName().toLowerCase());
            StringBuilder stringBuilder = new StringBuilder();
            if(logs.isEmpty()) {
                stringBuilder.append(foundPlayer.getLoginName()).append(" currently has no logs available to view here.");
            } else {
                for (String log : logs) {
                    stringBuilder.append(log).append("<br><br>");
                }
            }
            player.queue(() -> {
                player.getPA().sendString(68002, stringBuilder.toString());
            });
        });
    }

    public boolean handleButton(int buttonId) {
        if(buttonId == 135833) {
            updatePlayerList("");
            return true;
        }
        if(buttonId >= 65156 && buttonId <= 66156) {
            selectPlayer(buttonId - 65156);
            return true;
        }
        if(buttonId >= 68010 && buttonId <= 68033) {
            changeSkill(buttonId - 68010);
            return true;
        }
        return false;
    }

    private void changeSkill(int index) {
        Skill skill = Skill.values()[index];
        player.getPA().sendEnterAmount("You are now editing " + playerCurrentlyViewing.getLoginName() + "'s " + skill.name().toLowerCase() + " level.", (plr, level) -> {
            playerCurrentlyViewing.playerLevel[index] = level;
            playerCurrentlyViewing.playerXP[index] = playerCurrentlyViewing.getPA().getXPForLevel(level) + 1;
            if(PlayerHandler.getPlayerByLoginName(playerCurrentlyViewing.getLoginName()) != null) {
                playerCurrentlyViewing.getPA().refreshSkill(index);
                playerCurrentlyViewing.getPA().setSkillLevel(index, playerCurrentlyViewing.playerLevel[index], playerCurrentlyViewing.playerXP[index]);
            }
            PlayerSave.saveGame(playerCurrentlyViewing);
            updatePlayerDetails(playerCurrentlyViewing);
            submitLog(player.getLoginName().toLowerCase(),
                    playerCurrentlyViewing.getLoginName() + " had their " + skill.name().toLowerCase() + " level changed too " + level + " by " + player.getDisplayNameFormatted() +
                            " on !date!.");
        });
    }

    public boolean handleTextInput(int id, String playerName) {
        if(id != 65103)
            return false;
        currentPlayerNameFilter = playerName;
        updatePlayerList(currentPlayerNameFilter);
        return true;
    }

    public boolean handleDropdownMenuOption(int widgetId, int option) {
        if(widgetId == 65104) {

            return true;
        }
        if(widgetId == 65105) {
           onlineStatusFilter = OnlineStatusFilter.values()[option];
           updatePlayerList(currentPlayerNameFilter);
            return true;
        }
        return false;
    }

    enum OnlineStatusFilter {
        ONLINE,
        OFFLINE,
        BOTH
    }
    enum PlayerStatFilter {
        A_TO_Z,
        COMBAT_LEVEL,
        TOTAL_LEVEL,
        RANK,
    }

    @PostInit
    public static void setupDatabaseTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("player_panel_logs")
                .addColumn("id", TableType.INT, TableProperties.AUTO_INCREMENT, TableProperties.PRIMARY_KEY)
                .addColumn("player_name", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("log_message", TableType.TEXT, TableProperties.NOT_NULL)
                .addColumn("timestamp", TableType.BIGINT, TableProperties.NOT_NULL)
                ;

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }
    public static void submitLog(String playerName, String message) {
        message = message.replaceAll("!date!", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("player_panel_logs")
                .columns("player_name", "log_message", "timestamp")
                .values("?", "?", "?");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        String finalMessage = message;
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
                preparedStatement.setString(2, finalMessage);
                preparedStatement.setLong(3, System.currentTimeMillis());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public static List<String> getLogs(String playerName) {
        List<String> playerLogs = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        QueryBuilder queryBuilder = new QueryBuilder()
                .select("log_message")
                .from("player_panel_logs")
                .where("player_name = '" + playerName + "'");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    playerLogs.add(resultSet.getString("log_message"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await();  // Wait until the result processing is done
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        return playerLogs;
    }
}
