package io.kyros.runescript.action.impl;

import io.kyros.runescript.ScriptContext;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import io.kyros.runescript.ScriptInterpreter;
import io.kyros.runescript.action.Action;

public class DelayAction implements Action {
    private int cycles;

    public DelayAction(int cycles) {
        this.cycles = cycles;
    }

    @Override
    public void execute(ScriptContext context) {
        System.out.println("DelayAction for " + cycles + " cycles");
        context.setPaused(true);
        TaskManager.submit(new Task(cycles) {
            @Override
            protected void execute() {
                context.setPaused(false);
                ScriptInterpreter.resumeScript(context);
                stop();
            }
        });
    }
}

