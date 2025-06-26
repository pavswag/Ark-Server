package io.kyros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.kyros.content.event.eventcalendar.ChallengeParticipant;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.DatabaseTable;
import io.kyros.sql.SqlQuery;
import io.kyros.sql.eventcalendar.tables.EventCalendarBlacklistTable;

public class CheckForBlacklistQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarBlacklistTable();

    private final ChallengeParticipant participant;

    public CheckForBlacklistQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("select * from " + TABLE.getName()
             + " where " + EventCalendarBlacklistTable.IP_ADDRESS + "='" + participant.getIpAddress() + "' "
             + " or " + EventCalendarBlacklistTable.MAC_ADDRESS + "='" + participant.getMacAddress() + "'");
        return rs.next();
    }
}
