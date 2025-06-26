package io.kyros.sql.MainSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class IntParameter extends Parameter<Integer> {

    public IntParameter(int parameterId, Integer value) {
        super(parameterId, value);
    }

    @Override
    public void set(PreparedStatement statement) throws SQLException {
        statement.setInt(getParameterId(), super.getValue());
    }

}
