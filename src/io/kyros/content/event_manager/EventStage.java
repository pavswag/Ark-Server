package io.kyros.content.event_manager;

public enum EventStage {

    NOT_STARTED(-1, null),
    SECONDS_60(0, "The event is starting in 60 seconds! Type ::event to join the event."),
    SECONDS_30(1000 * 30, "The event is starting in 30 seconds! Type ::event to join the event."),
    SECONDS_10(1000 * 50, "The event is starting in 10 seconds! Type ::event to join the event."),
    SECONDS_5(1000 * 55, "The event is starting in 5 seconds! GET READY!!"),
    START_MESSAGE(1000 * 60, "The event has started! GO!!!!!!"),
    STARTED(1000 * 60, null);

    public long eventTimer;
    public String message;

    EventStage(long eventTimer, String message) {
        this.eventTimer = eventTimer;
        this.message = message;
    }

    public EventStage getNextStage() {
        if(this == STARTED) {
            return STARTED;
        }
        return values()[ordinal() + 1];
    }

}
