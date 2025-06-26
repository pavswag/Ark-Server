package io.kyros.mysql;

public enum SQLKeyword {
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    SQLKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return keyword;
    }
}

