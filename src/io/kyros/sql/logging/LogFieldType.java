package io.kyros.sql.logging;

import io.kyros.sql.MainSql.IntParameter;
import io.kyros.sql.MainSql.LongParameter;
import io.kyros.sql.MainSql.Parameter;
import io.kyros.sql.MainSql.StringParameter;

/**
 * Created by luke on 7/25/2019
 **/
public enum LogFieldType {

    STRING {
        @Override
        public Parameter getParameter(int index, Object data) {
            return new StringParameter(index, String.valueOf(data));
        }
    },
    INTEGER {
        @Override
        public Parameter getParameter(int index, Object data) {
            return new IntParameter(index, (int) data);
        }
    },
    LONG {
        @Override
        public Parameter getParameter(int index, Object data) {
            return new LongParameter(index, (long) data);
        }
    };

    public Parameter getParameter(int index, Object data) {
        return null;
    }
}
