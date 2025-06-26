package io.kyros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;

import io.kyros.content.event.eventcalendar.ChallengeParticipant;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.DatabaseTable;
import io.kyros.sql.SqlQuery;
import io.kyros.sql.eventcalendar.tables.EventCalendarBlacklistTable;

public class RemoveFromBlacklistQuery implements SqlQuery<Boolean> {

    private static final DatabaseTable TABLE = new EventCalendarBlacklistTable();

    private final ChallengeParticipant participant;

    public RemoveFromBlacklistQuery(ChallengeParticipant participant) {
        this.participant = participant;
    }

    @Override
    public Boolean execute(DatabaseManager context, Connection connection) throws SQLException {
        connection.createStatement().execute("delete from " + TABLE.getName() + " where "
                + EventCalendarBlacklistTable.MAC_ADDRESS + "='" + participant.getMacAddress() + "' and " +
                EventCalendarBlacklistTable.IP_ADDRESS + "='" + participant.getIpAddress() + "'");
        return true;
    }

}
