package io.kyros.mysql;

public enum SQLFunction {
    COUNT("COUNT(*)"),
    SUM("SUM"),
    AVG("AVG"),
    MIN("MIN"),
    MAX("MAX");

    private final String function;

    SQLFunction(String function) {
        this.function = function;
    }

    @Override
    public String toString() {
        return function;
    }
}


