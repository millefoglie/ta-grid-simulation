package com.github.millefoglie.gridsim.component;

import com.github.millefoglie.gridsim.entity.Entity;
import com.github.millefoglie.gridsim.grid.Movement;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Component for scheduling entity actions, e.g. queuing movements
 */
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
