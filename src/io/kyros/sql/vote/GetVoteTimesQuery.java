package io.kyros.sql.vote;

import io.kyros.model.entity.player.Player;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class GetVoteTimesQuery implements SqlQuery<Map<Integer, Timestamp>> {

    private final Player player;

    public GetVoteTimesQuery(final Player player) {
        this.player = player;
    }

    @Override
    public Map<Integer, Timestamp> execute(DatabaseManager context, Connection connection) throws Exception {
        Map<Integer, Timestamp> map = new HashMap<>();
        ResultSet rs = connection.createStatement().executeQuery(VoteThrottlerCheckQuery.select(player, -1));

        while (rs.next()) {
            int siteId = rs.getInt("site_id");
            Timestamp claimed = rs.getTimestamp("date_claimed");
            map.put(siteId, claimed);
        }

        return map;
    }
}
