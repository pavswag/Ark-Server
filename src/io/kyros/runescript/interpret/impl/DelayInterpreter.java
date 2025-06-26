package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.DelayAction;
import io.kyros.runescript.interpret.Interpreter;

public class DelayInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("p_delay(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        int cycles = Integer.parseInt(cleanUp(line.substring(8, line.length() - 1)));
        return new DelayAction(cycles);
    }
}
