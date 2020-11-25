package com.github.millefoglie.gridsim.event;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultEventBus implements EventBus {
    private final Map<Class<? extends Event>, List<Event>> eventRegistry = new ConcurrentHashMap<>();

    public DefaultEventBus() {}

    @Override
    public void register(Event event) {
        List<Event> eventsOfClass = eventRegistry.computeIfAbsent(event.getClass(), t -> new CopyOnWriteArrayList<>());
        eventsOfClass.add(event);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> Collection<T> findAllByClass(Class<T> eventClass) {
        return (List<T>) eventRegistry.getOrDefault(eventClass, Collections.emptyList());
    }

    @Override
    public void clear() {
        eventRegistry.clear();
    }
}
