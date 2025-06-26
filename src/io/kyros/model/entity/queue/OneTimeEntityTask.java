package io.kyros.model.entity.queue;

public class OneTimeEntityTask extends EntityTask {
    public OneTimeEntityTask(Runnable action, Priority priority) {
        super(action, priority);
    }
    public OneTimeEntityTask(Runnable action) {
        super(action);
    }
}
