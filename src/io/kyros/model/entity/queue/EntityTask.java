package io.kyros.model.entity.queue;

abstract class EntityTask {
    protected Runnable action;
    protected Priority priority;
    public EntityTask() {
        this.priority = Priority.NORMAL;
    }
    public EntityTask(Runnable action, Priority priority) {
        this.action = action;
        this.priority = priority;
    }
    public EntityTask(Runnable action) {
        this.action = action;
        this.priority = Priority.NORMAL;
    }

    public Priority getPriority() {
        return priority;
    }


}
