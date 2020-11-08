package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class DefaultComponentManager implements ComponentManager {
    private final Map<Class<?>, List<Component>> componentRegistry = new HashMap<>();

    @Override
    public <T extends Component> T createComponent(Entity entity, Class<T> componentClass) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(componentClass);

        T component;

        try {
            Constructor<T> constructor = componentClass.getConstructor(Entity.class);
            component = constructor.newInstance(entity);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        componentRegistry.computeIfAbsent(componentClass, t -> new CopyOnWriteArrayList<>()).add(component);
        entity.setComponent(componentClass, component);
        return component;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> findAllByClass(Class<T> componentClass) {
        return (List<T>) componentRegistry.getOrDefault(componentClass, Collections.emptyList());
    }

}
