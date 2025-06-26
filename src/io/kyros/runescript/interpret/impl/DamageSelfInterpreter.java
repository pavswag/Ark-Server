package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.DamageSelfAction;
import io.kyros.runescript.interpret.Interpreter;

public class DamageSelfInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("~damage_self(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        int damage = Integer.parseInt(cleanUp(line.substring(13, line.length() - 1)));
        return new DamageSelfAction(damage);
    }
}
