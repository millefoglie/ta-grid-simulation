package com.github.millefoglie.gridsim.event;

import java.util.Collection;

public interface EventBus {
    void register(Event event);
    <T extends Event> Collection<T> findAllByClass(Class<T> eventClass);
    void clear();
}
