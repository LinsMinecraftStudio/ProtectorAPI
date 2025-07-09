package io.github.lijinhong11.protector.api.protection;

import org.bukkit.event.Event;

public interface FakeEventMaker {
    boolean isEventFake(Event event);
}
