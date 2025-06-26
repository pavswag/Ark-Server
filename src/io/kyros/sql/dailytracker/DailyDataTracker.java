package io.kyros.sql.dailytracker;

import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.util.DataStorage;
import io.kyros.util.Misc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Handles daily data tracking for the Kyros application.
 * Tracks votes, donations, player counts, and various in-game events.
 * Also handles daily data insertion into the database and sending of daily summary emails.
 *
 * @since 18/03/2024
 */
public class DailyDataTracker {

    private static final String USERNAME = "OlympusNew";
    private static final String PASSWORD = "5uL2yuf8B13e";
    private static final String IP_ADDRESS = "51.222.84.54";
    private static final String DATABASE_NAME = "arkcane_game";
    public static boolean ENABLED = false;

    private static synchronized Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + IP_ADDRESS + "/" + DATABASE_NAME;
        return DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    /**
     * Inserts the daily data into the database.
     */
    public static void insertData() {
        if (!ENABLED) {
            return;
        }

        new Thread(() -> {
            String query = "INSERT INTO daily_tracker (votes, donations, real_online, new_joins, donor_boss, vote_boss, afk_boss, durial, groot, cox, tob, arbo, upgrade_attempts, nomad_spent, time_dumped) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, TrackerType.VOTES.getTrackerData());
                statement.setInt(2, TrackerType.DONATIONS.getTrackerData());
                statement.setInt(3, PlayerHandler.getUniquePlayerCount());
                statement.setInt(4, TrackerType.NEW_JOINS.getTrackerData());
                statement.setInt(5, TrackerType.DONOR_BOSS.getTrackerData());
                statement.setInt(6, TrackerType.VOTE_BOSS.getTrackerData());
                statement.setInt(7, TrackerType.AFK_BOSS.getTrackerData());
                statement.setInt(8, TrackerType.DURIAL.getTrackerData());
                statement.setInt(9, TrackerType.GROOT.getTrackerData());
                statement.setInt(10, TrackerType.COX.getTrackerData());
                statement.setInt(11, TrackerType.TOB.getTrackerData());
                statement.setInt(12, TrackerType.ARBO.getTrackerData());
                statement.setLong(13, TrackerType.UPGRADE_ATTEMPTS.getTrackerData());
                statement.setLong(14, TrackerType.NOMAD_SPENT.getTrackerData());
                statement.setString(15, Misc.getTime());

                statement.executeUpdate();
                resetTrackerData();
            } catch (SQLException e) {
                System.err.println("Error inserting daily data: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Resets the tracker data for the next day.
     */
    private static void resetTrackerData() {
        new Thread(() -> {
            for (TrackerType trackerType : TrackerType.values()) {
                trackerType.setTrackerData(0);
                trackerType.getUniqueData().clear();
                trackerType.getUniqueData2().clear();
                DataStorage.saveData(trackerType.name(), trackerType.getTrackerData());
            }
        }).start();
    }

    /**
     * Adds unique data to the tracker type.
     *
     * @param trackerType The type of tracker.
     * @param uniqueData  The unique data to add.
     * @param uniqueData2 The secondary unique data to add.
     */
    public static void addUniqueData(TrackerType trackerType, String uniqueData, String uniqueData2) {
        if (trackerType.getUniqueData().contains(uniqueData) || trackerType.getUniqueData2().contains(uniqueData2)) {
            return;
        }

        trackerType.getUniqueData().add(uniqueData);
        trackerType.getUniqueData2().add(uniqueData2);
    }

    /**
     * Sends a daily tracker summary email.
     *
     * @throws IOException If there is an error sending the email.
     */
    public static void sendDailyTrackerEmail() throws IOException {
        LocalDate currentDate = LocalDate.now();

        StringBuilder emailMessage = new StringBuilder()
                .append("Daily Votes: ").append(TrackerType.VOTES.getTrackerData()).append("\n")
                .append("Daily Donations: ").append(TrackerType.DONATIONS.getTrackerData()).append("\n")
                .append("Real Online: ").append(PlayerHandler.getUniquePlayerCount()).append("\n")
                .append("New Joins: ").append(TrackerType.NEW_JOINS.getTrackerData()).append("\n")
                .append("Donor Bosses: ").append(TrackerType.DONOR_BOSS.getTrackerData()).append("\n")
                .append("Vote Bosses: ").append(TrackerType.VOTE_BOSS.getTrackerData()).append("\n")
                .append("AFK Bosses: ").append(TrackerType.AFK_BOSS.getTrackerData()).append("\n")
                .append("Durial Bosses: ").append(TrackerType.DURIAL.getTrackerData()).append("\n")
                .append("Groot Bosses: ").append(TrackerType.GROOT.getTrackerData()).append("\n");

        EmailManager.sendEmail("Kyros Daily Tracker [" + currentDate.getMonth() + " " + currentDate.getDayOfMonth() + ", " + currentDate.getYear() + "]", emailMessage.toString());
        resetTrackerData(); // Reset tracker data after sending the email
    }

    public static LocalDate today = LocalDate.now();

    /**
     * Checks if a new day has started and if so, performs daily reset actions.
     */
    public static void newDay() {
        LocalDate now = LocalDate.now();
        if (today == null) {
            today = LocalDate.now();
            DataStorage.saveData("today", today.toString());
        }
        if (!today.equals(now) && ENABLED) {
            today = now;

            new Thread(() -> {
                try {
                    sendDailyTrackerEmail();
                } catch (IOException e) {
                    System.err.println("Error sending daily tracker email: " + e.getMessage());
                    e.printStackTrace();
                }

                insertData();
                resetTrackerData();
            }).start();

            DataStorage.saveData("today", today.toString());
        }
    }
}
