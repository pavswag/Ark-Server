package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.CloseWidgetsAction;
import io.kyros.runescript.action.impl.DeleteItemAction;
import io.kyros.runescript.interpret.Interpreter;

public class DeleteItemInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("~delete_item");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        return new DeleteItemAction();
    }
}
