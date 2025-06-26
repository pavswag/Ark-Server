package io.kyros.model.cycleevent;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class CycleEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(CycleEventHandler.class);
    private static final CycleEventHandler instance = new CycleEventHandler(); // Simplified singleton initialization

    public static CycleEventHandler getSingleton() {
        return instance;
    }

    private final Queue<CycleEventContainer> pending;
    private final Queue<CycleEventContainer> events; // Use ConcurrentLinkedQueue for efficient add/remove operations
    private final Set<Object> owners = ConcurrentHashMap.newKeySet(); // Thread-safe key set

    public CycleEventHandler() {
        this.pending = new ConcurrentLinkedQueue<>();
        this.events = new ConcurrentLinkedQueue<>();
    }
    public List<CycleEventContainer> getActiveEvents() {
        return new ArrayList<>(events); // Return a copy to avoid direct modifications
    }
    public CycleEventContainer addEvent(Object owner, CycleEvent event, int cyclesBetweenExecution) {
        return addEvent(owner, event, cyclesBetweenExecution, false);
    }

    public CycleEventContainer addEvent(Object owner, CycleEvent event, int cyclesBetweenExecution, boolean immediateExecution) {
        return addEvent(-1, owner, event, cyclesBetweenExecution, immediateExecution);
    }

    public CycleEventContainer addEvent(int id, Object owner, CycleEvent event, int cyclesBetweenExecution) {
        return addEvent(id, owner, event, cyclesBetweenExecution, false);
    }

    public CycleEventContainer addEvent(int id, Object owner, CycleEvent event, int cyclesBetweenExecution, boolean immediateExecution) {
        CycleEventContainer cycleEventContainer = new CycleEventContainer(id, owner, event, cyclesBetweenExecution);
        if (immediateExecution) {
            try {
                event.execute(cycleEventContainer);
            } catch (Exception e) {
                logger.error("An error occurred during event immediate execution, task has been stopped.", e);
                cycleEventContainer.stop();
                return null;
            }
        }
        if (cycleEventContainer.isRunning()) {
            this.pending.add(cycleEventContainer);
        }
        return cycleEventContainer;
    }

    public void addEvent(CycleEventContainer container) {
        pending.add(container);
    }

    public static long processTime = 0;

    public void process() {
        long start = System.currentTimeMillis();

        // Add pending events to the main event queue
        CycleEventContainer container;
        while ((container = pending.poll()) != null) {
            if (container.isRunning()) {
                events.add(container);
            }
        }

        updateOwnerSet();

        // Process events
        List<CycleEventContainer> randomizedEvents = new ArrayList<>();
        for (CycleEventContainer eventContainer : events) {
            try {
                if (eventContainer.isRunning()) {
                    eventContainer.update();
                    if (eventContainer.needsExecution()) {
                        if (eventContainer.isRandomized()) {
                            randomizedEvents.add(eventContainer);
                        } else {
                            eventContainer.execute();
                        }
                    }
                }
                if (!eventContainer.isRunning()) {
                    events.remove(eventContainer); // Safe removal in ConcurrentLinkedQueue
                }
            } catch (Exception e) {
                eventContainer.stop();
                events.remove(eventContainer); // Safe removal in ConcurrentLinkedQueue
                logger.error("An error occurred while processing tasks, task has been stopped.", e);
            }
        }

        if (!randomizedEvents.isEmpty()) {
            Collections.shuffle(randomizedEvents); // Consider optimizing or avoiding shuffle if unnecessary
            for (CycleEventContainer eventContainer : randomizedEvents) {
                try {
                    eventContainer.execute();
                } catch (Exception e) {
                    eventContainer.stop();
                    logger.error("An error occurred while processing randomized tasks, task has been stopped.", e);
                }
            }
        }

        updateOwnerSet();
        processTime = System.currentTimeMillis() - start;
    }

    private void updateOwnerSet() {
        owners.clear();
        for (CycleEventContainer event : events) {
            if (event.getOwner() != null) {
                owners.add(event.getOwner());
            }
        }
    }

    public void stopEvents(Object owner) {
        stopEventsInternal(owner, -1);
    }

    public void stopEvents(Object owner, int id) {
        if (id == -1) {
            throw new IllegalArgumentException("Illegal identification value, -1 is not permitted.");
        }
        stopEventsInternal(owner, id);
    }

    public void stopEvents(int id) {
        if (id == -1) {
            throw new IllegalArgumentException("Illegal identification value, -1 is not permitted.");
        }
        stopEventsInternal(null, id);
    }

    private void stopEventsInternal(Object owner, int id) {
        events.removeIf(container -> {
            boolean shouldStop = (owner == null || container.getOwner().equals(owner)) && (id == -1 || container.getID() == id);
            if (shouldStop) {
                container.stop();
            }
            return shouldStop;
        });

        pending.removeIf(container -> {
            boolean shouldStop = (owner == null || container.getOwner().equals(owner)) && (id == -1 || container.getID() == id);
            if (shouldStop) {
                container.stop();
            }
            return shouldStop;
        });
    }

    public boolean isAlive(Object owner) {
        return owners.contains(owner);
    }

    public interface Event {
        int BONE_ON_ALTAR = 10010;
        int OVERLOAD_BOOST_ID = 10020;
        int OVERLOAD_HITMARK_ID = 10025;
        int DIVINE_COMBAT_POTION = 10040;
        int DIVINE_ATTACK_POTION = 10041;
        int DIVINE_STRENGTH_POTION = 10042;
        int DIVINE_DEFENCE_POTION = 10043;
        int DIVINE_RANGE_POTION = 10045;
        int DIVINE_MAGIC_POTION = 10050;
        int SKILLING = 10049;

        int INF_PRAYER = 10060;
        int INF_ARGO = 10061;
        int RAGE = 10062;
        int PYRAMID_PLUNDER = 10063;
        int BlastFurnaceCoffer = 10064;
        int WaterFallAdd = 10065;
        int WaterFallRemove = 10066;
        int PayDirtSend = 10067;
        int WheelMotherlode = 10068;
        int AFKZone = 10069;
        int WEEKLYDONOS = 10070;
        int BONUSITEMS = 10071;
    }
}
