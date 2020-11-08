package com.github.millefoglie.system;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.component.ComponentManager;
import com.github.millefoglie.component.SchedulerComponent;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.event.TransformationRequestedEvent;

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
