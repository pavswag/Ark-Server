package io.kyros.sql.eventcalendar.queries;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.common.base.Preconditions;
import io.kyros.content.event.eventcalendar.ChallengeParticipant;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.SqlQuery;
import io.kyros.sql.eventcalendar.tables.EventCalendarParticipantsTable;

public class AddParticipantQuery implements SqlQuery<Object> {

    private static final EventCalendarParticipantsTable TABLE = new EventCalendarParticipantsTable();

    private final ChallengeParticipant participant;
    private final int amount;

    public AddParticipantQuery(ChallengeParticipant participant, int amount) {
        this.participant = participant;
        this.amount = amount;
    }

    @Override
    public Object execute(DatabaseManager context, Connection connection) throws SQLException {
        Preconditions.checkState(amount > 0);
        Statement statement = connection.createStatement();
        for (int count = 0; count < amount; count++) {
            statement.execute(
                    "INSERT INTO " + TABLE.getName()
                            + " VALUES ("
                            + "'" + participant.getUsername().toLowerCase() + "',"
                            + "'" + participant.getIpAddress() + "',"
                            + "'" + participant.getMacAddress() + "',"
                            + participant.getEntryDay()
                            + ")"
            );
        }
        return null;
    }
}
