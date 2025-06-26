package io.kyros.util.task;

import com.google.common.base.Preconditions;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.IntConsumer;

public final class TaskManager {

    private final static Logger LOGGER = LogManager.getLogger(TaskManager.class.getName());

    public final static Queue<Task> pendingTasks = new LinkedList<>();

    public final static List<Task> activeTasks = new LinkedList<>();


    public static long processTime = 0;
    public static void sequence() {
        long startTime = System.currentTimeMillis();
        try {

            Task task;
            while ((task = pendingTasks.poll()) != null) {
                if (task.isRunning()) {
                    activeTasks.add(task);
                }
            }
            Iterator<Task> it = activeTasks.iterator();

            while (it.hasNext()) {
                final long start = System.nanoTime();
                task = it.next();
                if (!task.tick())
                    it.remove();
                final long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            }

        } catch (Throwable t) {
            LOGGER.error("Exception occurred during processing of tasks", t);
        }
        processTime = System.currentTimeMillis() - startTime;
    }

    public static void submit(Task task) {
        if (!task.isRunning())
            return;
        task.onSubmit();
        if (task.isImmediate()) {
            task.execute();
        }
        pendingTasks.add(task);
    }

    /**
     * Submit a repeating {@link Task}.
     *
     * @param delay     the amount of cycles between each iteration
     * @param duration  the total amount of iterations (exclusive)
     * @param runnable  the functional interface being consumed (accepts the current cycle as a param)
     */
    public static void submit(int delay, int duration, IntConsumer runnable) {
        Preconditions.checkArgument(duration > 0, "Duration of the task must be more than 0 cycles");
        pendingTasks.add(new Task(delay) {
            int cycle = 0;
            @Override
            protected void execute() {

                runnable.accept(cycle);

                if(++cycle >= duration)
                    stop();
            }
        });
    }

    public static void submit(int duration, IntConsumer runnable, int delay) {
        Preconditions.checkArgument(duration > 0, "Duration of the task must be more than 0 cycles");
        pendingTasks.add(new Task(delay) {
            int cycle = 0;
            @Override
            protected void execute() {

                runnable.accept(cycle);

                if(++cycle >= duration)
                    stop();
            }
        });
    }

    /**
     * Submit a single execution {@link Task}.
     *
     * @param delay     the amount of cycles till execution
     * @param runnable  the functional interface being consumed (accepts the current cycle as a param)
     */
    public static void submit(Object lock, int delay, Runnable runnable) {
        pendingTasks.add(new Task(delay, lock, false) {
            @Override
            protected void execute() {
                stop();
                runnable.run();
            }
        });
    }

    public static void submit(int delay, Runnable runnable) {
        submit(Task.DEFAULT_KEY, delay, runnable);
    }

    public static void cancelTasks(Object key) {
        try {
            pendingTasks.stream().filter(t -> t.getKey().equals(key)).forEach(Task::stop);
            activeTasks.stream().filter(t -> t.getKey().equals(key)).forEach(Task::stop);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTaskAmount() {
        return (pendingTasks.size() + activeTasks.size());
    }

    public static String printPendingTasks() {
        return (pendingTasks.toString());
    }

    public static String printActiveTasks() {
        return (activeTasks.toString());
    }

    public static List<Task> getActiveTasks() {
        return activeTasks;
    }

}
