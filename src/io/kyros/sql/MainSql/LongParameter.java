package io.kyros.sql.MainSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LongParameter extends Parameter<Long> {

    public LongParameter(int parameterId, long value) {
        super(parameterId, value);
    }

    @Override
    public void set(PreparedStatement statement) throws SQLException {
        statement.setLong(getParameterId(), super.getValue());
    }
}
