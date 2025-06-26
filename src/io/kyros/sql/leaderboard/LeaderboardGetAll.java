package io.kyros.sql.leaderboard;

import io.kyros.content.leaderboards.LeaderboardEntry;
import io.kyros.content.leaderboards.LeaderboardType;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.SqlQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardGetAll implements SqlQuery<List<LeaderboardEntry>> {

    private final LeaderboardType type;

    public LeaderboardGetAll(LeaderboardType type) {
        this.type = type;
    }

    @Override
    public List<LeaderboardEntry> execute(DatabaseManager context, Connection connection) throws SQLException {
        ArrayList<LeaderboardEntry> entries = new ArrayList<>();

        PreparedStatement leaders = connection.prepareStatement("SELECT * FROM leaderboards "
                + "INNER JOIN display_names on display_names.login_name = leaderboards.username"
                + " where type = " + type.ordinal()
                + " ORDER BY date DESC, amount DESC"
        );

        ResultSet rs = leaders.executeQuery();
        while (rs.next()) {
            String loginName = rs.getString("username");
            String displayName = rs.getString("display_name");
            long amount = rs.getLong("amount");
            int type = rs.getInt("type");
            Date date = rs.getDate("date");
            entries.add(new LeaderboardEntry(LeaderboardType.values()[type], loginName, displayName, amount, date.toLocalDate().
                    atStartOfDay()));
        }

        return entries;
    }

}
