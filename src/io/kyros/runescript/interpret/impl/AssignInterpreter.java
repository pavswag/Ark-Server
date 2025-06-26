package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.AssignAction;
import io.kyros.runescript.interpret.Interpreter;

public class AssignInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("def_int");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String[] parts = line.split(" ");
        String varName = parts[1];
        String expression = cleanUp(line.substring(line.indexOf("=") + 1)).replace(";", "");
        return new AssignAction(varName, expression);
    }
}
