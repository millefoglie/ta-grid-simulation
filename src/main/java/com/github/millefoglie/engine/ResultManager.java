package com.github.millefoglie.engine;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.component.TransformationComponent;
import com.github.millefoglie.entity.EntityManager;
import com.github.millefoglie.entity.EntityType;
import com.github.millefoglie.grid.Point;

public class ResultManager {
    private final EntityManager entityManager;

    public ResultManager() {
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.entityManager = appCtx.getBean(EntityManager.class);
    }

    public void print() {
        entityManager.findAllByType(EntityType.MOWER)
                     .stream()
                     .map(e -> e.getComponent(TransformationComponent.class))
                     .map(this::formatPosition)
                     .forEach(System.out::println);
    }

    private String formatPosition(TransformationComponent component) {
        Point point = component.getStagePoint();
        char orientationChar = component.getOrientation().name().charAt(0);

        return String.format("%s %s %s", point.getX(), point.getY(), orientationChar);
    }
}
