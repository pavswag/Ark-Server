package io.kyros.model.entity.queue;

import java.util.function.Consumer;

public class RepeatingEntityTask extends EntityTask {
    private boolean finished = false;
    private int delay = 0;

    public RepeatingEntityTask(Consumer<RepeatingEntityTask> repeatAction) {
        super();
        self = this;
        Runnable action = () -> {
            if (delay > 0) {
                delay--;
            } else {
                repeatAction.accept(self);
            }
        };
        this.action = action;
    }

    private final RepeatingEntityTask self;

    public void finish() {
        finished = true;
    }

    public boolean isFinished() {
        return finished;
    }

    public void wait(int ticks) {
        this.delay = ticks;
    }
}

