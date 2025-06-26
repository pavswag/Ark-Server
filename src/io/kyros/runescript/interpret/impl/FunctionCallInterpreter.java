package io.kyros.runescript.interpret.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptLoader;
import io.kyros.runescript.action.Action;
import io.kyros.runescript.interpret.Interpreter;

import java.util.List;

public class FunctionCallInterpreter implements Interpreter {
    @Override
    public boolean canInterpret(String line) {
        return line.startsWith("@");
    }

    @Override
    public Action interpret(String line, ScriptContext context) {
        String functionName = line.substring(1, line.indexOf('(')).trim();
        List<String> functionLines = ScriptLoader.getGlobalFunction(functionName);
        if (functionLines != null) {
            context.interpret(functionLines);
        } else {
            System.out.println("Function not found: " + functionName);
        }
        return null;
    }
}
