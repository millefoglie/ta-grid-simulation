package com.github.millefoglie.gridsim.component;

import com.github.millefoglie.gridsim.entity.Entity;

import java.util.Objects;

public class AbstractComponent implements Component {
    private final Entity entity;

    public AbstractComponent(Entity entity) {
        Objects.requireNonNull(entity);
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }

    @Override
    public String toString() {
        return "AbstractComponent{" +
                "entity.id=" + entity.getId() +
                '}';
    }
}
