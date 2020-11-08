package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;

public class AbstractComponent implements Component {
    private final Entity entity;

    public AbstractComponent(Entity entity) {
        this.entity = entity;
    }

    @Override
    public Entity getEntity() {
        return entity;
    }
}
