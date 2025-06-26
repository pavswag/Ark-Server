package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.AnimAction;
import io.kyros.runescript.interpret.Interpreter;

public class AnimInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("anim(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String[] parts = line.substring(5, line.length() - 1).split(",");
        return new AnimAction(parts[0].trim(), Integer.parseInt(cleanUp(parts[1]).trim()));
    }
}
