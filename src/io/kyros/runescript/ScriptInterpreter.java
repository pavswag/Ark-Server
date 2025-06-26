package io.kyros.runescript;

import io.kyros.runescript.action.Action;
import io.kyros.runescript.interpret.Interpreter;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.regex.Pattern;

public class ScriptInterpreter {
    private static final List<Interpreter> interpreters = new ArrayList<>();

    static {
        loadInterpreters();
    }

    private static void loadInterpreters() {
        Reflections reflections = new Reflections("io.kyros.runescript.interpret.impl");
        Set<Class<? extends Interpreter>> classes = reflections.getSubTypesOf(Interpreter.class);
        for (Class<? extends Interpreter> clazz : classes) {
            try {
                interpreters.add(clazz.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Loaded [" + interpreters.size() + "] interpreters");
    }

    public void interpret(List<String> scriptLines, ScriptContext context) {
        for (String line : scriptLines) {
            if (!line.startsWith("[label,")) {
                Action action = parseLine(line, context);
                if (action != null) {
                    context.addAction(action);
                }
            }
        }
        executeNextAction(context);
    }

    private Action parseLine(String line, ScriptContext context) {
        for (Interpreter interpreter : interpreters) {
            if (interpreter.canInterpret(line)) {
                return interpreter.interpret(line, context);
            }
        }
        System.out.println("Unrecognised script instruction [" + line + "]");
        return null;
    }

    public static void resumeScript(ScriptContext context) {
        if (context.isPaused()) {
            return;
        }
        executeNextAction(context);
    }

    private static void executeNextAction(ScriptContext context) {
        while (context.hasNextAction() && !context.isPaused()) {
            Action action = context.getNextAction();
            action.execute(context);
        }
    }
}
