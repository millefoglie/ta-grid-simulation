package com.github.millefoglie.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEventBus implements EventBus {
    private final Map<Class<? extends Event>, List<Event>> eventRegistry = new HashMap<>();

    public DefaultEventBus() {}

    @Override
    public void register(Event event) {
        List<Event> eventsOfClass = eventRegistry.computeIfAbsent(event.getClass(), t -> new ArrayList<>());
        eventsOfClass.add(event);
    }

    @Override
    @SuppressWarnings("ALL")
    public <T extends Event> Collection<T> findAllByClass(Class<T> eventClass) {
        return (List<T>) eventRegistry.getOrDefault(eventClass, Collections.emptyList());
    }

    @Override
    public void clear() {
        eventRegistry.forEach((k, v) -> v.clear());
    }
}
