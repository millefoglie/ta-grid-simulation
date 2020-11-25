package com.github.millefoglie.gridsim.entity;

import com.github.millefoglie.gridsim.component.Component;

import java.util.HashMap;
import java.util.Map;

public class DefaultEntity implements Entity {
    private final int id;
    private final EntityType type;
    private final Map<Class<?>, Component> components = new HashMap<>(2);

    public DefaultEntity(int id, EntityType type) {
        this.id = id;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public EntityType getType() {
        return type;
    }

    @Override
    public <T extends Component> void setComponent(Class<T> componentClass, T component) {
        components.put(componentClass, component);
    }

    @Override
    @SuppressWarnings("ALL")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }
}
