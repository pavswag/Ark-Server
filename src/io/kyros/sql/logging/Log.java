package io.kyros.sql.logging;

import io.kyros.Configuration;
import io.kyros.sql.MainSql.Parameter;
import io.kyros.sql.MainSql.SQLNetwork;
import io.kyros.sql.MainSql.SQLTable;

import java.util.ArrayList;

public class Log {

    public Log() {

    }

    public SQLTable getSqlTable() {
        return null;
    }

    public String getSchemaTable() {
        return SQLTable.getGameSchemaTable(getSqlTable());
    }

    public void submit(LogField... data) {

        int totalFields = data.length;

        StringBuilder query = new StringBuilder();

        query.append("INSERT INTO " + getSchemaTable() + " (");

        int fieldsProcessed = 0;

        for (LogField logField : data) {
            query.append(logField.getTitle());

            if (fieldsProcessed < totalFields - 1) {
                query.append(",");
            }

            fieldsProcessed++;
        }

        query.append(") VALUES(");

        fieldsProcessed = 0;

        ArrayList<Parameter> parameters = new ArrayList<>();

        for (LogField logField : data) {

            parameters.add(logField.getLogFieldType().getParameter(fieldsProcessed + 1, logField.getData()));

            query.append("?");

            if (fieldsProcessed < totalFields - 1) {
                query.append(",");
            }

            fieldsProcessed++;
        }

        query.append(")");

        if(Configuration.DISABLE_DATABASES) {
            SQLNetwork.insert(
                    query.toString(),
                    parameters.toArray(new Parameter[0])
            );
        }
    }

}