package io.kyros.model.cycleevent;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Predicate;

/**
 * A class that manages events that are pulsed every game cycle.
 *
 * @author Jason MacKeigan
 * @date Jan 12, 2015, 5:15:04 PM
 */
public class EventHandler {

	/**
	 * A queue of all the events that need to be added to the list of running events.
	 */
	private final Queue<Event<?>> pendingAddition = new ConcurrentLinkedQueue<>();

	/**
	 * A queue of all the events that need to be removed from the list of running events.
	 */
	private final Queue<Event<?>> pendingRemoval = new ConcurrentLinkedQueue<>();

	/**
	 * A queue of the currently running events.
	 */
	private final Queue<Event<?>> active = new ConcurrentLinkedQueue<>();

	/**
	 * Submits a new event to be added.
	 *
	 * @param event the event
	 */
	public <T> void submit(Event<T> event) {
		Objects.requireNonNull(event, "Event cannot be null");
		pendingAddition.add(event);
	}

	/**
	 * Stops any and all events that have a common attachment.
	 *
	 * @param attachment the attachment that the event must have
	 */
	public <T> void stop(T attachment) {
		Predicate<Event<?>> equalTo = event -> Objects.equals(event.getAttachment(), attachment);
		enqueueForRemoval(equalTo);
	}

	/**
	 * Stops any and all events that have a common attachment and signature.
	 *
	 * @param attachment the attachment that the event must have
	 * @param signature  the signature the event must have
	 */
	public <T> void stop(T attachment, String signature) {
		Predicate<Event<?>> equalTo = event -> Objects.equals(event.getAttachment(), attachment)
				&& Objects.equals(event.getSignature(), signature);
		enqueueForRemoval(equalTo);
	}

	/**
	 * Stops all events with the same signature that are active or are being added to the active list.
	 *
	 * @param signature the signature of the events being compared to.
	 */
	public void stop(String signature) {
		Predicate<Event<?>> predicate = event -> Objects.equals(event.getSignature(), signature);
		enqueueForRemoval(predicate);
	}

	/**
	 * Determines if an event with the same attachment and signature is active.
	 *
	 * @param attachment the attachment of the event
	 * @param signature  the signature of the event
	 * @return {@code true} if the event is found and meets the predicate terms, otherwise {@code false}.
	 */
	public <T> boolean isRunning(T attachment, String signature) {
		Predicate<Event<?>> running = event -> event.isAlive()
				&& Objects.equals(event.getAttachment(), attachment)
				&& Objects.equals(event.getSignature(), signature);
		return active.stream().anyMatch(running) || pendingAddition.stream().anyMatch(running);
	}

	/**
	 * Processes each of the events that are currently running.
	 */
	public static long processTime = 0;

	public void process() {
		long start = System.currentTimeMillis();

		// Move pending additions to active
		if (!pendingAddition.isEmpty()) {
			active.addAll(pendingAddition);
			pendingAddition.clear();
		}

		// Process pending removals
		while (!pendingRemoval.isEmpty()) {
			Event<?> event = pendingRemoval.poll();
			if (event != null && event.isAlive()) {
				event.stop();
			}
		}

		// Process active events
		Queue<Event<?>> remainingEvents = new ConcurrentLinkedQueue<>(); // Using ConcurrentLinkedQueue to reduce locking overhead
		while (!active.isEmpty()) {
			Event<?> event = active.poll();
			if (event == null || event.requiresTermination() || event.getAttachment() == null) {
				if (event != null) {
					event.stop();
				}
				continue;
			}

			if (event.isAlive()) {
				event.increaseElapsed();
				event.update();

				if (event.isAlive()) {
					if (event.getTicks() > 1) {
						event.removeTick();
						remainingEvents.add(event);
					} else if (event.getTicks() <= 1) {
						event.execute();
						event.reset();
						remainingEvents.add(event);
					}
				}
			}
		}

		// Re-add remaining events to active
		active.addAll(remainingEvents);

		processTime = System.currentTimeMillis() - start;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Events Active:\n");
		active.forEach(e -> sb.append("--> ").append(e.getAttachment()).append(" : ").append(e.getSignature()).append("\n"));
		sb.append("[adding = ").append(pendingAddition.size()).append(", active = ").append(active.size()).append(", removing = ").append(pendingRemoval.size()).append("]");
		return sb.toString();
	}

	/**
	 * Helper method to enqueue events for removal based on a predicate.
	 *
	 * @param predicate the condition to select events for removal
	 */
	private void enqueueForRemoval(Predicate<Event<?>> predicate) {
		active.stream().filter(predicate).forEach(pendingRemoval::add);
		pendingAddition.stream().filter(predicate).forEach(pendingRemoval::add);
	}
}
