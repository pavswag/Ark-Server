package io.kyros.util.task;

import java.util.Objects;

public abstract class Task {

    /**
     * The default key for every task.
     */
    public static final Object DEFAULT_KEY = new Object();

    /**
     * A flag which indicates if this task should be executed once immediately.
     */
    private final boolean immediate;

    /**
     * The number of cycles between consecutive executions of this task.
     */
    private int delay;

    /**
     * The current 'count down' value. When this reaches zero the task will be
     * executed.
     */
    private int countdown;

    /**
     * The number of times the task has been {@link #execute() executed}.
     */
    private int iterations = 0;

    /**
     * A flag which indicates if this task is still running.
     */
    protected boolean running = true;

    /**
     * The task's owner
     */
    private Object key;

    private final StackTraceElement[] stackTraceElements;

    /**
     * Creates a new task with a {@link #delay} of 1 cycle.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task() {
        this(1);
    }

    /**
     * Creates a new task with the specified delay.
     *
     * @param delay     The number of cycles between consecutive executions of this task.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(int delay) {
        this(delay, false);
    }

    /**
     * Creates a new task with a delay of 1 cycle and immediate flag.
     *
     * @param immediate A flag that indicates if the first execution
     *                  should be immediately when {@link TaskManager#submit(Task) submitted}.
     */
    public Task(boolean immediate) {
        this(1, immediate);
    }


    /**
     * Creates a new task with the specified delay and immediate flag.
     *
     * @param delay     The number of cycles between consecutive executions of this task.
     * @param immediate A flag that indicates if the first execution
     *                  should be immediately when {@link TaskManager#submit(Task) submitted}.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(int delay, boolean immediate) {
        this(delay, DEFAULT_KEY, immediate);
    }

    /**
     * Creates a new task with the specified delay and immediate flag.
     *
     * @param delay     The number of cycles between consecutive executions of this
     *                  task.
     * @param immediate A flag which indicates if for the first execution there
     *                  should be no delay.
     * @throws IllegalArgumentException if the {@code delay} is not positive.
     */
    public Task(int delay, Object key, boolean immediate) {
        this.delay = delay;
        this.countdown = delay;
        this.immediate = immediate;
        this.bind(key);
        stackTraceElements = Thread.currentThread().getStackTrace();
    }


    /**
     * Binds this task to the argued object.
     *
     * @param key the {@link Object} that can be used
     *            to {@link TaskManager#cancelTasks(Object) cancel} this {@link Task}.
     * @return this {@link Task}.
     * @throws NullPointerException if key is {@code null}.
     */
    public final Task bind(Object key) {
        this.key = Objects.requireNonNull(key);
        return this;
    }

    /**
     * This method should be called by the scheduling class every cycle. It
     * updates the {@link #countdown} and calls the {@link #execute()} method
     * if necessary.
     *
     * @return A flag indicating if the task is {@link #running}.
     */
    public boolean tick() {
        if (running && --countdown == 0) {
            execute();
            iterations++;
            countdown = delay;
        }
        return running;
    }

    /**
     * Called when this task has been submitted to the {@link TaskManager}.
     */
    public void onSubmit() {

    }

    /**
     * Performs this task's action.
     */
    protected abstract void execute();

    /**
     * Changes the delay of this task.
     *
     * @param delay The number of cycles between consecutive executions of this task.
     */
    public void setDelay(int delay) {
        if (delay > 0)
            this.delay = delay;
    }

    /**
     * Stops this task.
     */
    public void stop() {
        running = false;
        onStop();
    }

    /**
     * Called when this task has been stopped.
     */
    public void onStop() {

    }

    public void setEventRunning(boolean running) {
        this.running = running;
    }

    /**
     * Checks if this task is an immediate task.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * Checks if the task is running.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Checks if the task is stopped.
     *
     * @return {@code true} if so, {@code false} if not.
     */
    public boolean isStopped() {
        return !running;
    }

    public boolean isStale() {
        return iterations > 100;
    }

    public final Object getKey() {
        return Objects.requireNonNull(key);
    }

    public int getDelay() {
        return this.delay;
    }

    public int getExecutionCount() {
        return iterations;
    }

    public boolean isAnonymousTask(){
        return getClass().isAnonymousClass();
    }

    @Override
    public String toString() {
        return "Task{" +
                "name=" + getClassName() +
                ", immediate=" + immediate +
                ", delay=" + delay +
                ", countdown=" + countdown +
                ", iterations=" + iterations +
                ", running=" + running +
                ", key=" + key +
                ", origin=" + findDeclaringClass() +
                '}';
    }

    private String getClassName() {
        if (getClass().isAnonymousClass())
            return "Anonymous";
        else
            return getClass().getSimpleName();
    }


    public StackTraceElement findDeclaringClass(){
        boolean inConstructor = false;
        for (StackTraceElement element: stackTraceElements){
            final String className = element.getClassName();
            if (className.equals(Task.class.getName())){
                inConstructor = true;
            } else if (inConstructor) {
                return element;
            }
        }
        return null;
    }
}
