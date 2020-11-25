package com.github.millefoglie.gridsim.event;

import com.github.millefoglie.gridsim.entity.Entity;
import com.github.millefoglie.gridsim.grid.Movement;

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
