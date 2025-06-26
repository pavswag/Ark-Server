package io.kyros.sql.MainSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DoubleParameter extends Parameter<Double> {

    public DoubleParameter(int parameterId, double value) {
        super(parameterId, value);
    }

    @Override
    public void set(PreparedStatement statement) throws SQLException {
        statement.setDouble(getParameterId(), super.getValue());
    }
}
