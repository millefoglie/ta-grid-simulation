package com.github.millefoglie.event;

import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.grid.Movement;

public class TransformationRequestedEvent implements Event {
    private final Entity entity;
    private final Movement movement;

    public TransformationRequestedEvent(Entity entity, Movement movement) {
        this.entity = entity;
        this.movement = movement;
    }

    public Entity getEntity() {
        return entity;
    }

    public Movement getMovement() {
        return movement;
    }
}
