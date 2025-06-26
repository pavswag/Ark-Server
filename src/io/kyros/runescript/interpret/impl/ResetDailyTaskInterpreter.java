package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.MesAction;
import io.kyros.runescript.action.impl.ResetDailyTaskAction;
import io.kyros.runescript.interpret.Interpreter;

public class ResetDailyTaskInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("-reset_daily_task(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String message = cleanUp(line.substring(18, line.length() - 1));
        boolean resetScroll = Boolean.parseBoolean(message);
        return new ResetDailyTaskAction(resetScroll);
    }
}
