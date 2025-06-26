package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.StatementAction;
import io.kyros.runescript.interpret.Interpreter;

public class StatementInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("~statement(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String message = cleanUp(line.substring(11, line.length() - 1));
        return new StatementAction(message);
    }
}
