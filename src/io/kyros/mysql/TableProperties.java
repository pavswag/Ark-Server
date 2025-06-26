package io.kyros.mysql;

public enum TableProperties {
    NOT_NULL("NOT NULL"),
    AUTO_INCREMENT("AUTO_INCREMENT"),
    PRIMARY_KEY("PRIMARY KEY"),
    UNIQUE("UNIQUE"),
    DEFAULT("DEFAULT"),
    CHECK("CHECK"),
    REFERENCES("REFERENCES");

    private final String sql;

    TableProperties(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return sql;
    }
}



