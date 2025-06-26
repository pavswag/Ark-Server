package io.kyros.runescript.action.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.util.Misc;

import java.util.Random;

public class AssignAction implements Action {
    private final String varName;
    private final String expression;

    public AssignAction(String varName, String expression) {
        this.varName = varName;
        this.expression = expression;
    }

    @Override
    public void execute(ScriptContext context) {
        int value = evaluateExpression(context);
        context.setIntVariable(varName, value);
        System.out.println("Assigned int " + varName + " = " + value);
        context.resume();
    }

    private int evaluateExpression(ScriptContext context) {
        if (expression.startsWith("random(")) {
            String range = expression.substring(7).trim();
            System.out.println("Finding random number between 0 and " + range + " on expression =" + expression);
            return Misc.random(Integer.parseInt(range));
        }
        return Integer.parseInt(expression);
    }
}
