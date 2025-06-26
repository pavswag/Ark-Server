package io.kyros.model.entity.queue;

import io.kyros.model.entity.Entity;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@RequiredArgsConstructor
public class EntityQueue {
    private final Entity entity;

    public List<OneTimeEntityTask> oneTimeTasks = new ArrayList<>();
    public List<RepeatingEntityTask> repeatingTasks = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    // Using a fixed thread pool to limit the number of concurrent threads based on your system capacity
    private final ExecutorService executor = Executors.newFixedThreadPool(16); // Adjust pool size based on your hardware

    public void queue(OneTimeEntityTask task) {
        lock.lock();
        try {
            oneTimeTasks.add(task);
            handlePriorities(); // Handle task priorities immediately
        } finally {
            lock.unlock();
        }
    }

    public void repeat(RepeatingEntityTask task) {
        lock.lock();
        try {
            repeatingTasks.add(task);
        } finally {
            lock.unlock();
        }
    }

    private void handlePriorities() {
        boolean hasStrongTask = oneTimeTasks.stream().anyMatch(t -> t.getPriority() == Priority.STRONG);
        if (hasStrongTask) {
            oneTimeTasks.removeIf(t -> t.getPriority() == Priority.WEAK);
        }
    }

    public void processTasks(boolean playerHasModalInterface) {
        List<OneTimeEntityTask> tasksToRun = new ArrayList<>();
        List<RepeatingEntityTask> repeatingTasksToRun = new ArrayList<>();

        lock.lock();
        try {
            // Handle strong task priorities (removing weak tasks)
            boolean hasStrongTask = oneTimeTasks.stream().anyMatch(t -> t.getPriority() == Priority.STRONG);
            if (hasStrongTask) {
                oneTimeTasks.removeIf(t -> t.getPriority() == Priority.WEAK);
            }

            // Collect tasks to process and clear the original lists
            tasksToRun.addAll(oneTimeTasks);
            repeatingTasksToRun.addAll(repeatingTasks);
            oneTimeTasks.clear();
            repeatingTasks.removeIf(RepeatingEntityTask::isFinished);
        } finally {
            lock.unlock();
        }

        // Process one-time tasks
        tasksToRun.removeIf(task -> task.getPriority() == Priority.NORMAL && playerHasModalInterface);
        tasksToRun.forEach(task -> {
            executor.submit(() -> {
                try {
                    entity.performTask(task.action);
                } catch (Exception e) {
                    e.printStackTrace();  // Error handling
                }
            });
        });

        // Process repeating tasks
        repeatingTasksToRun.removeIf(task -> task.getPriority() == Priority.NORMAL && playerHasModalInterface);
        repeatingTasksToRun.forEach(task -> {
            executor.submit(() -> {
                try {
                    entity.performTask(task.action);
                } catch (Exception e) {
                    e.printStackTrace();  // Error handling
                }
            });
        });
    }

    public void shutdown() {
        executor.shutdown(); // Graceful shutdown of the executor when the server stops
    }
}
