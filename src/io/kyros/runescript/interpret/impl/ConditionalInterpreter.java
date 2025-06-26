package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.ConditionalAction;
import io.kyros.runescript.interpret.Interpreter;

public class ConditionalInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("if (") || line.startsWith("while (") || line.startsWith("for (") || line.startsWith("} else if (") || line.startsWith("} else {");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        if (line.startsWith("if (")) {
            String condition = line.substring(4, line.indexOf(')')).trim();
            return new ConditionalAction(condition, "if");
        } else if (line.startsWith("} else if (")) {
            String condition = line.substring(11, line.indexOf(')')).trim();
            return new ConditionalAction(condition, "else if");
        } else if (line.startsWith("} else {")) {
            return new ConditionalAction("true", "else");
        } else if (line.startsWith("while (")) {
            String condition = line.substring(7, line.indexOf(')')).trim();
            context.addLoopStartIndex(context.getCurrentActionIndex());
            return new ConditionalAction(condition, "while");
        } else if (line.startsWith("for (")) {
            String condition = line.substring(5, line.indexOf(')')).trim();
            context.addLoopStartIndex(context.getCurrentActionIndex());
            return new ConditionalAction(condition, "for");
        }
        return null;
    }
}
