package io.kyros.content.votemanager;

import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.mysql.*;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import io.kyros.util.offlinestorage.ItemCollection;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.Getter;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class VoteManager {
    private static VoteManager instance;

    @Getter
    private final List<List<GameItem>> weeklyRewards = new ArrayList<>();
    @Getter
    private final List<List<GameItem>> monthlyRewards = new ArrayList<>();

    private boolean weeklyResetCompleted = false;
    private boolean monthlyResetCompleted = false;

    private VoteManager() {
     //   createTables();
        loadRewards("./etc/cfg/vote/rewards.yml");
    }

    public static VoteManager getInstance() {
        if (instance == null) {
            synchronized (VoteManager.class) {
                if (instance == null) {
               //     instance = new VoteManager();
                }
            }
        }
        return instance;
    }

    public void tick() {
        /*if (isTimeForWeeklyReset() && !weeklyResetCompleted) {
            resetWeeklyVotes();
            weeklyResetCompleted = true;
        }

        if (isTimeForMonthlyReset() && !monthlyResetCompleted) {
            resetMonthlyVotes();
            monthlyResetCompleted = true;
        }*/
    }

    private boolean isTimeForWeeklyReset() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextSunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .withHour(0).withMinute(0).withSecond(0)
                .truncatedTo(ChronoUnit.SECONDS);
        return now.isAfter(nextSunday);
    }

    private boolean isTimeForMonthlyReset() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime nextMonthFirst = now.with(TemporalAdjusters.firstDayOfNextMonth())
                .withHour(0).withMinute(0).withSecond(0)
                .truncatedTo(ChronoUnit.SECONDS);
        return now.isAfter(nextMonthFirst);
    }

    private void resetWeeklyVotes() {
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                // Step 1: Get the top 5 players for the week
                List<String> topPlayers = getTop5WeeklyPlayers();

                // Step 2: Distribute rewards
                distributeRewards(topPlayers, weeklyRewards, "weekly");

                System.out.println("Weekly reset executed.");
                weeklyResetCompleted = false; // Ready for next week's reset
                stop();
            }
        });
    }

    private void resetMonthlyVotes() {
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                // Step 1: Get the top 5 players for the month
                List<String> topPlayers = getTop5MonthlyPlayers();

                // Step 2: Distribute rewards
                distributeRewards(topPlayers, monthlyRewards, "monthly");

                // Step 3: Clear all votes in the database
                clearAllVotes();

                System.out.println("Monthly reset executed and all votes cleared.");
                monthlyResetCompleted = false; // Ready for next month's reset
                stop();
            }
        });
    }

    private void createTables() {
        createWinnersTable();
        createVotesTable();
    }

    private void createWinnersTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("vote_ranking_winners")
                .addColumn("id", TableType.INT, TableProperties.AUTO_INCREMENT, TableProperties.PRIMARY_KEY)
                .addColumn("player_name", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("reward_type", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("timestamp", TableType.BIGINT, TableProperties.NOT_NULL)
                .addColumn("vote_points", TableType.INT, TableProperties.NOT_NULL)
                .addColumn("rewards", TableType.TEXT, TableProperties.NOT_NULL);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }

    private void createVotesTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("votes")
                .addColumn("id", TableType.INT, TableProperties.AUTO_INCREMENT, TableProperties.PRIMARY_KEY)
                .addColumn("player_name", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("timestamp", TableType.BIGINT, TableProperties.NOT_NULL);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }

    public static void open(Player player) {
        player.getPA().runClientScript(13021, 23_750, false);
        player.getPA().showInterface(23_750);
        Misc.executorService.submit(() -> {
            List<String> topWeekly = getInstance().getTop5WeeklyPlayers();
            List<String> topMonthly = getInstance().getTop5MonthlyPlayers();
            Map<String, Integer> weeklyVoteCounts = new HashMap<>();
            topWeekly.forEach(plr -> weeklyVoteCounts.put(plr, getInstance().getWeeklyVoteCount(plr)));
            Map<String, Integer> monthlyVoteCounts = new HashMap<>();
            topMonthly.forEach(plr -> monthlyVoteCounts.put(plr, getInstance().getMonthlyVoteCount(plr)));
            TaskManager.submit(new Task(3) {
                @Override
                protected void execute() {
                    for (int i = 23761; i <= 23765; i++) {
                        int index = i - 23761;
                        int itemContainer = i + 5;
                        try {
                            player.getPA().sendString(i, topWeekly.get(index) + " - " + weeklyVoteCounts.get(topWeekly.get(index)) + " votes!");
                        } catch (Exception e) {
                            player.getPA().sendString(i, "N/A - 0 votes!");
                        }
                        for (int slot = 0; slot < 6; slot++) {
                            player.getPA().itemOnInterface(getInstance().weeklyRewards.get(index).get(slot).getId(), getInstance().weeklyRewards.get(index).get(slot).getAmount(), itemContainer, slot);
                        }
                    }
                    for (int i = 23779; i <= 23783; i++) {
                        int index = i - 23779;
                        int itemContainer = i + 5;

                        try {
                            player.getPA().sendString(i, topMonthly.get(index) + " - " + monthlyVoteCounts.get(topMonthly.get(index)) + " votes!");
                        } catch (Exception e) {
                            player.getPA().sendString(i, "N/A - 0 votes!");
                        }
                        for (int slot = 0; slot < 6; slot++) {
                            player.getPA().itemOnInterface(getInstance().monthlyRewards.get(index).get(slot).getId(), getInstance().monthlyRewards.get(index).get(slot).getAmount(), itemContainer, slot);
                        }
                    }
                    player.getPA().runClientScript(13021, 23_750, true);
                    stop();
                }
            });
        });
    }

    private void loadRewards(String filePath) {
        Yaml yaml = new Yaml();
        try (FileInputStream input = new FileInputStream(filePath)) {
            Map<String, List<List<List<Integer>>>> data = yaml.load(input);

            List<List<List<Integer>>> weeklyRewardsData = data.get("weekly_rewards");
            List<List<List<Integer>>> monthlyRewardsData = data.get("monthly_rewards");

            for (List<List<Integer>> rewardSet : weeklyRewardsData) {
                weeklyRewards.add(parseRewards(rewardSet));
            }
            for (List<List<Integer>> rewardSet : monthlyRewardsData) {
                monthlyRewards.add(parseRewards(rewardSet));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<GameItem> parseRewards(List<List<Integer>> rewardData) {
        List<GameItem> rewards = new ArrayList<>();
        for (List<Integer> item : rewardData) {
            rewards.add(new GameItem(item.get(0), item.get(1)));
        }
        return rewards;
    }

    public int getWeeklyRanking(String playerName) {
        long startTime = getStartOfCurrentWeek();
        return getPlayerRanking(playerName, startTime);
    }

    public int getMonthlyRanking(String playerName) {
        long startTime = getStartOfCurrentMonth();
        return getPlayerRanking(playerName, startTime);
    }

    private int getPlayerRanking(String playerName, long startTime) {
        List<String> topPlayers = getTopPlayers(startTime);
        return topPlayers.indexOf(playerName) + 1;
    }

    public void recordVote(String playerName) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("votes")
                .columns("player_name", "timestamp")
                .values("?", "?");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
                preparedStatement.setLong(2, Instant.now().toEpochMilli());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public List<String> getTop5WeeklyPlayers() {
        System.out.println("Getting weekly top 5 players");
        long startTime = getStartOfCurrentWeek();
        return getTopPlayers(startTime);
    }

    public List<String> getTop5MonthlyPlayers() {
        long startTime = getStartOfCurrentMonth();
        return getTopPlayers(startTime);
    }

    private List<String> getTopPlayers(long startTime) {
        List<String> topPlayers = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        QueryBuilder queryBuilder = new QueryBuilder()
                .select(SQLFunction.COUNT, "vote_count")
                .columns("player_name")
                .from("votes")
                .where("timestamp", SQLOperator.GREATER_THAN_OR_EQUALS, startTime)
                .groupBy("player_name")
                .orderBy("vote_count", SQLKeyword.DESC)
                .limit(5);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    topPlayers.add(resultSet.getString("player_name"));
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

        return topPlayers;
    }

    private long getStartOfCurrentWeek() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfWeek = now.with(ChronoField.DAY_OF_WEEK, 7) // Sunday
                .minusWeeks(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .truncatedTo(ChronoUnit.DAYS);
        return startOfWeek.toInstant().toEpochMilli();
    }

    private long getStartOfCurrentDay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);
        return startOfDay.toInstant().toEpochMilli();
    }

    private long getStartOfCurrentMonth() {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime startOfMonth = now.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
        return startOfMonth.toInstant().toEpochMilli();
    }

    private void distributeRewards(List<String> topPlayers, List<List<GameItem>> rewards, String rewardType) {
        // Create a list to store players and their vote counts
        List<Map.Entry<String, Integer>> playerVotes = new ArrayList<>();

        // Populate the list with player names and their respective vote counts
        for (String player : topPlayers) {
            int voteCount = getVoteCount(player);
            playerVotes.add(new AbstractMap.SimpleEntry<>(player, voteCount));
        }

        // Sort the list in descending order based on the vote count
        playerVotes.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Now distribute rewards based on the sorted order
        for (int i = 0; i < playerVotes.size(); i++) {
            String player = playerVotes.get(i).getKey();
            int voteCount = playerVotes.get(i).getValue();
            for (GameItem gameItem : rewards.get(i)) {
                ItemCollection.add(player, gameItem);

                Discord.writeServerSyncMessage("[VOTE LOG] TYPE:"+ rewardType + " User: " + player + " has " + voteCount + " votes & got given " + gameItem.getDef().getName() + " x " + gameItem.getAmount());
            }
            List<GameItem> rewardSet = rewards.get(i);
            String rewardsString = serializeRewards(rewardSet);
            recordWinner(player, rewardType, Instant.now().toEpochMilli(), voteCount, rewardsString);
            System.out.println("Rewarding player " + player + " with " + rewardSet);
            Discord.writeServerSyncMessage("[VOTE LOG] TYPE:"+ rewardType + " User: " + player + " has " + voteCount + " votes & got given " + rewardSet.toString());
        }
    }

    private String serializeRewards(List<GameItem> rewards) {
        StringBuilder sb = new StringBuilder();
        for (GameItem reward : rewards) {
            sb.append(reward.getId()).append(":").append(reward.getAmount()).append(",");
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private void recordWinner(String playerName, String rewardType, long timestamp, int votePoints, String rewards) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("vote_ranking_winners")
                .columns("player_name", "reward_type", "timestamp", "vote_points", "rewards")
                .values("?", "?", "?", "?", "?");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
                preparedStatement.setString(2, rewardType);
                preparedStatement.setLong(3, timestamp);
                preparedStatement.setInt(4, votePoints);
                preparedStatement.setString(5, rewards);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public int getVoteCount(String playerName) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select(SQLFunction.COUNT, "vote_count")
                .from("votes")
                .where("player_name = ?");

        final int[] voteCount = {0};
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executePreparedStatement(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, resultSet -> {
            try {
                if (resultSet.next()) {
                    voteCount[0] = (int) resultSet.getLong("vote_count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return voteCount[0];
    }

    public int getWeeklyVoteCount(String playerName) {
        long startTime = getStartOfCurrentWeek();
        return getVoteCountSince(playerName, startTime);
    }

    public int getMonthlyVoteCount(String playerName) {
        long startTime = getStartOfCurrentMonth();
        return getVoteCountSince(playerName, startTime);
    }

    public boolean hasVotedToday(String playerName) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select(SQLFunction.COUNT, "vote_count")
                .from("votes")
                .where("player_name = ?")
                .where("timestamp", SQLOperator.GREATER_THAN_OR_EQUALS, getStartOfCurrentDay());

        final int[] voteCount = {0};
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executePreparedStatement(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, resultSet -> {
            try {
                if (resultSet.next()) {
                    voteCount[0] = (int) resultSet.getLong("vote_count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return voteCount[0] > 0;
    }

    private int getVoteCountSince(String playerName, long startTime) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select(SQLFunction.COUNT, "vote_count")
                .from("votes")
                .where("player_name = ?")
                .where("timestamp", SQLOperator.GREATER_THAN_OR_EQUALS, startTime);

        final int[] voteCount = {0};
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executePreparedStatement(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, resultSet -> {
            try {
                if (resultSet.next()) {
                    voteCount[0] = (int) resultSet.getLong("vote_count");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return voteCount[0];
    }

    private void clearAllVotes() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .deleteFrom("votes");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }
}
