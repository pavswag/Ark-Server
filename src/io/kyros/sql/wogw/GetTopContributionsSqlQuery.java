package io.kyros.sql.wogw;

import io.kyros.content.wogw.WogwContribution;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.SqlQuery;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class GetTopContributionsSqlQuery implements SqlQuery<List<WogwContribution>> {

    private final int maximumResults;

    public GetTopContributionsSqlQuery(int maximumResults) {
        this.maximumResults = maximumResults;
    }

    @Override
    public List<WogwContribution> execute(DatabaseManager context, Connection connection) throws Exception {
        Statement statement = connection.createStatement();
        statement.setMaxRows(maximumResults);
        ResultSet rs = statement.executeQuery("SELECT wogw_total_contributions.total, display_names.display_name FROM wogw_total_contributions" +
                " INNER JOIN display_names ON display_names.login_name = wogw_total_contributions.login_name" +
                " ORDER BY total DESC"
        );

        List<WogwContribution> list = new ArrayList<>();
        while (rs.next()) {
            String displayName = rs.getString("display_name");
            long contribution = rs.getLong("total");
            list.add(new WogwContribution(displayName, contribution));
        }

        return list;
    }
}
