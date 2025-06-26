package io.kyros.sql.MainSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StringParameter extends Parameter<String> {

    public StringParameter(int parameterId, String value) {
        super(parameterId, value);
    }

    @Override
    public void set(PreparedStatement statement) throws SQLException {
        statement.setString(getParameterId(), getValue());
    }
}
