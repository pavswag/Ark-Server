package io.kyros.runescript.action.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public class ConditionalAction implements Action {
    private final String condition;
    private final String type;

    public ConditionalAction(String condition, String type) {
        this.condition = condition;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public void execute(ScriptContext context) {
        boolean result = context.evaluateCondition(condition);
        if (type.equals("if") || type.equals("else if") || type.equals("while") || type.equals("for")) {
            if (result) {
                System.out.println("Condition met: " + condition);
                context.setLastConditionResult(true);
            } else {
                System.out.println("Condition failed: " + condition);
                context.setLastConditionResult(false);
                context.skipToNextCondition();
            }
        } else if (type.equals("else")) {
            if (!context.getLastConditionResult()) {
                System.out.println("Executing else block");
                context.setLastConditionResult(true);
            } else {
                context.skipToNextCondition();
            }
        }

        if (type.equals("while") && result) {
            context.loopBackToStart();
        }

        if (type.equals("for") && result) {
            context.loopBackToStart();
        }

        context.resume();
    }
}
