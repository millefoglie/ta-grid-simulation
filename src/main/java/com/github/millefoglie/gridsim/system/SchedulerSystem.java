package com.github.millefoglie.gridsim.system;

import com.github.millefoglie.gridsim.ApplicationContext;
import com.github.millefoglie.gridsim.component.ComponentManager;
import com.github.millefoglie.gridsim.component.SchedulerComponent;
import com.github.millefoglie.gridsim.event.EventBus;
import com.github.millefoglie.gridsim.event.TransformationRequestedEvent;

/**
 * System that processed scheduled or queue entity actions
 */
public class SchedulerSystem implements GameSystem {
    private final ComponentManager componentManager;
    private final EventBus eventBus;

    public SchedulerSystem() {
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.componentManager = appCtx.getBean(ComponentManager.class);
        this.eventBus = appCtx.getBean(EventBus.class);
    }

    @Override
    public void update() {
        componentManager.findAllByClass(SchedulerComponent.class)
                        .parallelStream()
                        .map(c -> new TransformationRequestedEvent(c.getEntity(), c.pollMovement()))
                        .filter(e -> e.getMovement() != null)
                        .forEach(eventBus::register);
    }
}
