package com.github.millefoglie.gridsim.engine;

import com.github.millefoglie.gridsim.ApplicationContext;
import com.github.millefoglie.gridsim.component.TransformationComponent;
import com.github.millefoglie.gridsim.entity.EntityManager;
import com.github.millefoglie.gridsim.entity.EntityType;
import com.github.millefoglie.gridsim.grid.Point;

import java.io.PrintStream;

class ResultManager {
    private final EntityManager entityManager;

    ResultManager() {
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.entityManager = appCtx.getBean(EntityManager.class);
    }

    /**
     * Print each mower position in format X Y O, one entity per line
     */
    void print(PrintStream out) {
        entityManager.findAllByType(EntityType.MOWER)
                     .stream()
                     .map(e -> e.getComponent(TransformationComponent.class))
                     .map(this::formatPosition)
                     .forEach(out::println);
    }

    private String formatPosition(TransformationComponent component) {
        Point point = component.getStagePoint();
        char orientationChar = component.getOrientation().name().charAt(0);

        return String.format("%s %s %s", point.getX(), point.getY(), orientationChar);
    }
}
