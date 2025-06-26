package io.kyros.mysql;

import java.sql.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MockResultSet extends ResultSetAdapter {
    private final Iterator<Map<String, Object>> iterator;
    private Map<String, Object> currentRow;

    public MockResultSet(List<Map<String, Object>> data) {
        this.iterator = data.iterator();
    }

    @Override
    public boolean next() {
        if (iterator.hasNext()) {
            currentRow = iterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getString(String columnLabel) {
        return (String) currentRow.get(columnLabel);
    }

    @Override
    public int getInt(String columnLabel) {
        return (int) currentRow.get(columnLabel);
    }

    @Override
    public double getDouble(String columnLabel) {
        return (double) currentRow.get(columnLabel);
    }

    @Override
    public long getLong(String columnLabel) {
        return (long) currentRow.get(columnLabel);
    }

    @Override
    public Date getDate(String columnLabel) {
        return (Date) currentRow.get(columnLabel);
    }


    // Add other getXXX methods as needed
}

