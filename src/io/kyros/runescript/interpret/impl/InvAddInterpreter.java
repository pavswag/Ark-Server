package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.action.impl.InvAddAction;
import io.kyros.runescript.interpret.Interpreter;

public class InvAddInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("inv_add(");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String[] parts = line.substring(8, line.length() - 1).split(",");
        String inventoryType = parts[0].trim();
        String itemName = parts[1].trim();
        int amount = Integer.parseInt(cleanUp(parts[2]));
        return new InvAddAction(inventoryType, itemName, amount);
    }
}
