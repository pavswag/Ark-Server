package io.kyros.sql.leaderboard;

import io.kyros.content.leaderboards.LeaderboardEntry;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.SqlQuery;

import java.sql.*;

public class LeaderboardAdd implements SqlQuery<Boolean> {

    private final LeaderboardEntry entry;

    public LeaderboardAdd(LeaderboardEntry entry) {
        this.entry = entry;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        PreparedStatement insert = connection.prepareStatement("INSERT INTO leaderboards VALUES(?, ?, ?, curdate()) ON DUPLICATE KEY UPDATE amount = amount + ?");
        insert.setString(1, entry.getLoginName().toLowerCase());
        insert.setLong(2,  entry.getAmount());
        insert.setInt(3, entry.getType().ordinal());
        insert.setLong(4, entry.getAmount());
        insert.execute();
        return true;
    }
}
