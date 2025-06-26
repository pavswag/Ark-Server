package io.kyros.model.entity.player.trackers;

import com.google.gson.Gson;
import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.mysql.DatabaseManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Tracks player activity such as online time, idle time, messages sent, and boss kills.
 * The data is saved to the database upon player logout.
 */
@Getter
@Setter
@Slf4j
public class ActivityTracker {

    private final Player player;  // Reference to the player being tracked

    private LocalDateTime loginTime;  // DateTime the player logged in
    private LocalDateTime logoutTime; // DateTime the player logged out
    private long totalOnlineTime;     // Total time the player was online during the session (in milliseconds)

    private int clanChatMessages = 0;
    private int yellMessages = 0;
    private int privateMessages = 0;
    private int localChatMessages = 0;
    private long sessionStartTime;

    private long lastActivityTime; // Last recorded time of player activity
    private long totalIdleTime;    // Total time the player was idle during the session (in milliseconds)

    private final Map<String, Integer> bossKillCount = new HashMap<>(); // Tracks boss kills by boss name

    /**
     * Constructs an ActivityTracker for the specified player.
     *
     * @param player the player whose activity is being tracked
     */
    public ActivityTracker(Player player) {
        this.player = player;
    }

    /**
     * Records the player's login time and session start time.
     * This method should be called when the player logs in.
     */
    public void playerLoggedIn() {
        this.loginTime = LocalDateTime.now(ZoneOffset.UTC); // Use UTC for consistency
        this.sessionStartTime = System.currentTimeMillis();
    }

    /**
     * Records the player's logout time and updates the total online time.
     * This method should be called when the player logs out.
     */
    public void playerLoggedOut() {
        this.logoutTime = LocalDateTime.now(ZoneOffset.UTC); // Use UTC for consistency
        updateTotalOnlineTime();

        if (!Configuration.BETA_SERVER && !Configuration.DISABLE_DATABASES && player.getRights().isOrInherits(Right.HELPER)) {
            new Thread(this::saveToDatabase).start();//New thread database saving
        }
    }

    /**
     * Updates the total online time by calculating the difference between logout and login times.
     */
    private void updateTotalOnlineTime() {
        this.totalOnlineTime += (System.currentTimeMillis() - sessionStartTime);
    }

    /**
     * Updates the player's idle time based on activity.
     * If the player is idle, the idle time is increased.
     */
    public void updateActivity() {
        long currentTime = System.currentTimeMillis();
        if (player.isIdle) {
            long idleDuration = currentTime - lastActivityTime;
            totalIdleTime += idleDuration;
        }
        lastActivityTime = currentTime;  // Update last activity time
    }

    /**
     * Increments the boss kill count for the specified boss name.
     *
     * @param bossName the name of the boss killed
     */
    public void incrementBossKill(String bossName) {
        bossKillCount.put(bossName, bossKillCount.getOrDefault(bossName, 0) + 1);
    }

    // Methods to increment various types of chat messages
    public void incrementClanChatMessages() {
        clanChatMessages++;
    }

    public void incrementYellMessages() {
        yellMessages++;
    }

    public void incrementPrivateMessages() {
        privateMessages++;
    }

    public void incrementLocalChatMessages() {
        localChatMessages++;
    }

    /**
     * Calculates the average number of messages sent per minute for a given message count.
     *
     * @param totalMessages the total number of messages sent
     * @return the average number of messages per minute
     */
    public double getAverageMessagesPerMinute(int totalMessages) {
        long elapsedTime = System.currentTimeMillis() - sessionStartTime;
        double minutes = elapsedTime / 60000.0;
        return totalMessages / minutes;
    }

    public double getAverageClanChatMessagesPerMinute() {
        return getAverageMessagesPerMinute(clanChatMessages);
    }

    public double getAverageYellMessagesPerMinute() {
        return getAverageMessagesPerMinute(yellMessages);
    }

    public double getAverageLocalChatMessagesPerMinute() {
        return getAverageMessagesPerMinute(localChatMessages);
    }

    public double getAveragePrivateMessagesPerMinute() {
        return getAverageMessagesPerMinute(privateMessages);
    }

    /**
     * Formats the idle time in the format YY:MM:DD:HH:MM:SS.
     *
     * @param idleTimeMillis the idle time in milliseconds
     * @return the formatted idle time
     */
    public String formatTime(long idleTimeMillis) {
        long days = TimeUnit.MILLISECONDS.toDays(idleTimeMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(idleTimeMillis) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(idleTimeMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(idleTimeMillis) % 60;

        // Convert days into years and months
        long years = days / 365;
        days = days % 365;
        long months = days / 30;
        days = days % 30;

        return String.format("%02d:%02d:%02d:%02d:%02d:%02d", years, months, days, hours, minutes, seconds);
    }

    /**
     * Saves the tracked player activity data to the database.
     */
    public void saveToDatabase() {
        String sql = "INSERT INTO player_activity (player_name, total_online_time, total_idle_time, clan_chat_messages, " +
                "yell_messages, private_messages, local_chat_messages, average_clan_chat_messages_per_minute, " +
                "average_yell_messages_per_minute, average_private_messages_per_minute, " +
                "average_local_chat_messages_per_minute, boss_kill_count, login_time, logout_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DatabaseManager.getInstance().executeUpdate(sql, preparedStatement -> {
            try {
                preparedStatement.setString(1, player.getDisplayName().toLowerCase());
                String formattedOnlineTime = formatTime(totalOnlineTime);
                preparedStatement.setString(2, formattedOnlineTime);
                String formattedIdleTime = formatTime(totalIdleTime);
                preparedStatement.setString(3, formattedIdleTime);
                preparedStatement.setInt(4, clanChatMessages);
                preparedStatement.setInt(5, yellMessages);
                preparedStatement.setInt(6, privateMessages);
                preparedStatement.setInt(7, localChatMessages);
                preparedStatement.setDouble(8, getAverageClanChatMessagesPerMinute());
                preparedStatement.setDouble(9, getAverageYellMessagesPerMinute());
                preparedStatement.setDouble(10, getAveragePrivateMessagesPerMinute());
                preparedStatement.setDouble(11, getAverageLocalChatMessagesPerMinute());

                // Convert bossKillCount map to JSON string using Gson
                String bossKillCountJson = new Gson().toJson(bossKillCount);
                preparedStatement.setString(12, bossKillCountJson);

                // Convert LocalDateTime to SQL Timestamp and set it
                preparedStatement.setTimestamp(13, Timestamp.valueOf(loginTime));
                preparedStatement.setTimestamp(14, Timestamp.valueOf(logoutTime));

            } catch (SQLException e) {
                log.error("Failed to save player activity data for player: {}", player.getDisplayName(), e);
            }
        });
    }
}
