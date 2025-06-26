package io.kyros.mysql;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class QueryBuilder {
    private SQLOperation operation;
    private SelectFilter selectFilter = SelectFilter.ALL;
    private String table;
    private List<String> columns = new ArrayList<>();
    private List<String> values = new ArrayList<>();
    private List<String> conditions = new ArrayList<>();
    private List<String> updates = new ArrayList<>();
    private List<String> createTableColumns = new ArrayList<>();
    private List<String> primaryKeyColumns = new ArrayList<>();
    private List<String> groupByColumns = new ArrayList<>();
    private List<String> orderByColumns = new ArrayList<>();
    private List<String> orderByDirections = new ArrayList<>();
    private Integer limit;

    public QueryBuilder select(SQLFunction function, String alias) {
        this.operation = SQLOperation.SELECT;
        this.columns.add(function.toString() + " AS " + alias);
        return this;
    }

    public QueryBuilder select(String... columns) {
        this.operation = SQLOperation.SELECT;
        if (columns.length > 0) {
            for (String column : columns) {
                this.columns.add(column);
            }
        } else {
            this.columns.add("*");
        }
        return this;
    }

    public QueryBuilder select(SelectFilter filter, String... columns) {
        this.operation = SQLOperation.SELECT;
        this.selectFilter = filter;
        if (columns.length > 0) {
            for (String column : columns) {
                this.columns.add(column);
            }
        } else {
            this.columns.add("*");
        }
        return this;
    }

    public QueryBuilder select(SQLFunction function, String column, String alias) {
        this.operation = SQLOperation.SELECT;
        this.columns.add(function.toString() + "(" + column + ") AS " + alias);
        return this;
    }

    public QueryBuilder insertInto(String table) {
        this.operation = SQLOperation.INSERT;
        this.table = table;
        return this;
    }

    public QueryBuilder update(String table) {
        this.operation = SQLOperation.UPDATE;
        this.table = table;
        return this;
    }

    public QueryBuilder deleteFrom(String table) {
        this.operation = SQLOperation.DELETE;
        this.table = table;
        return this;
    }

    public QueryBuilder createTable(String table) {
        this.operation = SQLOperation.CREATE;
        this.table = table;
        return this;
    }

    public QueryBuilder columns(String... columns) {
        for (String column : columns) {
            this.columns.add(column);
        }
        return this;
    }

    public QueryBuilder values(String... values) {
        for (String value : values) {
            this.values.add(value);
        }
        return this;
    }

    public QueryBuilder set(String column, String value) {
        this.updates.add(column + " = " + value);
        return this;
    }

    public QueryBuilder where(String condition) {
        this.conditions.add(condition);
        return this;
    }

    public QueryBuilder where(String column, SQLOperator operator, long value) {
        this.conditions.add(column + " " + operator + " " + value);
        return this;
    }

    public QueryBuilder addColumn(String name, TableType type, TableProperties... properties) {
        StringJoiner columnDefinition = new StringJoiner(" ");
        if (type == TableType.VARCHAR) {
            columnDefinition.add(name).add(type.toString() + "(255)");
        } else {
            columnDefinition.add(name).add(type.toString());
        }

        for (TableProperties property : properties) {
            columnDefinition.add(property.toString());
        }

        this.createTableColumns.add(columnDefinition.toString());
        return this;
    }

    public QueryBuilder primaryKey(String... columns) {
        for (String column : columns) {
            this.primaryKeyColumns.add(column);
        }
        return this;
    }

    public QueryBuilder groupBy(String... columns) {
        for (String column : columns) {
            this.groupByColumns.add(column);
        }
        return this;
    }

    public QueryBuilder orderBy(String column, SQLKeyword direction) {
        this.orderByColumns.add(column);
        this.orderByDirections.add(direction.toString());
        return this;
    }

    public QueryBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public String build() {
        StringJoiner sql = new StringJoiner(" ");

        switch (operation) {
            case SELECT:
                sql.add("SELECT");
                if (selectFilter != SelectFilter.ALL) {
                    sql.add(selectFilter.toString());
                }
                sql.add(String.join(", ", columns)).add("FROM").add(table);
                break;
            case INSERT:
                sql.add("INSERT INTO").add(table).add("(").add(String.join(", ", columns)).add(")").add("VALUES").add("(").add(String.join(", ", values)).add(")");
                break;
            case UPDATE:
                sql.add("UPDATE").add(table).add("SET").add(String.join(", ", updates));
                break;
            case DELETE:
                sql.add("DELETE FROM").add(table);
                break;
            case CREATE:
                sql.add("CREATE TABLE IF NOT EXISTS").add(table).add("(").add(String.join(", ", createTableColumns));
                if (!primaryKeyColumns.isEmpty()) {
                    sql.add(", PRIMARY KEY (").add(String.join(", ", primaryKeyColumns)).add(")");
                }
                sql.add(")");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + operation);
        }

        if (!conditions.isEmpty()) {
            sql.add("WHERE").add(String.join(" AND ", conditions));
        }

        if (!groupByColumns.isEmpty()) {
            sql.add("GROUP BY").add(String.join(", ", groupByColumns));
        }

        if (!orderByColumns.isEmpty()) {
            sql.add("ORDER BY");
            for (int i = 0; i < orderByColumns.size(); i++) {
                if (i > 0) {
                    sql.add(",");
                }
                sql.add(orderByColumns.get(i) + " " + orderByDirections.get(i));
            }
        }

        if (limit != null) {
            sql.add("LIMIT").add(limit.toString());
        }
        return sql.toString();
    }

    public QueryBuilder from(String table) {
        this.table = table;
        return this;
    }

    public boolean isSelectQuery() {
        return operation == SQLOperation.SELECT;
    }
}
