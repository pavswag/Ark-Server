package io.kyros.runescript;

import io.kyros.runescript.ScriptContext;
import io.kyros.util.Misc;

public class ExpressionEvaluator {
    public static int evaluate(String expression, ScriptContext context) {
        expression = expression.trim();
        if (expression.startsWith("random(") && expression.endsWith(")")) {
            int bound = Integer.parseInt(expression.substring(7, expression.length() - 1).trim());
            return Misc.random(bound);
        }
        // Add more expression handling as needed
        return 0; // Default return value if expression is not recognized
    }
}

