package io.github.lijinhong11.protector.api.protection;

import org.bukkit.event.Event;

public interface FakeEventMaker {
    /**
     * Check the event is fake
     * @param event the event
     * @return true if the event is fake, false otherwise
     */
    boolean isEventFake(Event event);
}
