package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.CloseWidgetsAction;
import io.kyros.runescript.action.impl.ResetDailyTaskAction;
import io.kyros.runescript.interpret.Interpreter;

public class CloseWidgetsInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("~close_widgets");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        return new CloseWidgetsAction();
    }
}
