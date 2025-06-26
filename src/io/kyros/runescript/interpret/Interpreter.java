package io.kyros.runescript.interpret;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;

public interface Interpreter {
    boolean canInterpret(String line);
    Action interpret(String line, ScriptContext context);

     default String cleanUp(String str) {
        return str.replace(")", "").trim();
    }
}
