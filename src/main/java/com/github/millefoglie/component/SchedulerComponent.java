package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.grid.Movement;

import java.util.LinkedList;
import java.util.Queue;

public class SchedulerComponent extends AbstractComponent {
    private final Queue<Movement> movementQueue = new LinkedList<>();

    public SchedulerComponent(Entity entity) {
        super(entity);
    }

    public void offerMovement(Movement movement) {
        movementQueue.offer(movement);
    }

    public Movement pollMovement() {
        return movementQueue.poll();
    }
}
