package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.ChatPlayerAction;
import io.kyros.runescript.interpret.Interpreter;

public class ChatPlayerInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("~chatplayer(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String message = cleanUp(line.substring(12, line.length() - 1));
        return new ChatPlayerAction(message);
    }
}
