package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultComponentManager implements ComponentManager {
    private final Map<Class<?>, List<Component>> componentRegistry = new HashMap<>();

    @Override
    public <T extends Component> T createComponent(Entity entity, Class<T> componentClass) {
        T component = null;

        try {
            Constructor<T> constructor = componentClass.getConstructor(Entity.class);
            component = constructor.newInstance(entity);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        List<Component> componentsOfClass = componentRegistry.computeIfAbsent(componentClass, t -> new ArrayList<>());

        componentsOfClass.add(component);
        entity.setComponent(componentClass, component);
        return component;
    }

}
