package io.kyros.content.event_manager;


import io.kyros.content.event_manager.impl.*;
import io.kyros.util.Misc;

public enum EventType {

    DIG_EVENT(new DigEvent()),
    DROP_PARTY(new DropPartyEvent())

    ;

    private Event event;

    EventType(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public String getName() {
        return Misc.capitalize(name().replaceAll("_", " "));
    }
}
