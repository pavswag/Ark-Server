package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.MesAction;
import io.kyros.runescript.interpret.Interpreter;

public class MesInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("mes(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String message = cleanUp(line.substring(4, line.length() - 1));
        return new MesAction(message);
    }
}
