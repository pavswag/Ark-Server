package io.kyros.sql.MainSql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/03/2024
 */
public abstract class Parameter<T> {

    private final int parameterId;

    private final T value;

    public Parameter(int parameterId, T value) {
        this.parameterId = parameterId;
        this.value = value;
    }

    public abstract void set(PreparedStatement statement) throws SQLException;

    public int getParameterId() {
        return parameterId;
    }

    public T getValue() {
        return value;
    }
}