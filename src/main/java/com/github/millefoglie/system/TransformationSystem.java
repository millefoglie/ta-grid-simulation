package com.github.millefoglie.system;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.component.ComponentManager;
import com.github.millefoglie.component.TransformationComponent;
import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.entity.EntityManager;
import com.github.millefoglie.entity.EntityType;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.event.GameOverEvent;
import com.github.millefoglie.event.TransformationRequestedEvent;
import com.github.millefoglie.grid.Grid;
import com.github.millefoglie.grid.Movement;
import com.github.millefoglie.grid.Orientation;
import com.github.millefoglie.grid.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TransformationSystem implements GameSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final ComponentManager componentManager;
    private final EventBus eventBus;
    private final Grid grid;

    public TransformationSystem() {
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.componentManager = appCtx.getBean(ComponentManager.class);
        this.eventBus = appCtx.getBean(EventBus.class);
        this.grid = appCtx.getBean(Grid.class);
    }

    @Override
    public void update() {
        Collection<TransformationRequestedEvent> events =
                eventBus.findAllByClass(TransformationRequestedEvent.class);

        if (events.isEmpty()) {
            eventBus.register(new GameOverEvent());
            return;
        }

        handleEvents(events);
        refreshGrid();
        resolveCollisions();
        flip();

        if (LOGGER.isTraceEnabled()) {
            ApplicationContext.getInstance()
                              .getBean(EntityManager.class)
                              .findAllByType(EntityType.MOWER)
                              .stream()
                              .map(e -> e.getComponent(TransformationComponent.class))
                              .map(TransformationComponent::toString)
                              .forEach(LOGGER::trace);
        }
    }

    private void handleEvents(Collection<TransformationRequestedEvent> events) {
        events.parallelStream()
              .forEach(this::updatePosition);
    }

    private void updatePosition(TransformationRequestedEvent event) {
        Entity entity = event.getEntity();
        TransformationComponent component = entity.getComponent(TransformationComponent.class);
        Movement movement = event.getMovement();

        switch (movement) {
        case LEFT:
        case RIGHT:
            component.setOrientation(rotate(component.getOrientation(), movement));
            break;
        case FORWARD:
            component.setStagePoint(translateForward(component.getCurrentPoint(), component.getOrientation()));
            break;
        }
    }

    private void refreshGrid() {
        Map<Point, List<TransformationComponent>> gridMap = grid.getGridMap();

        gridMap.clear();
        componentManager.findAllByClass(TransformationComponent.class)
                        .parallelStream()
                        .forEach(c -> putOnGrid(gridMap, c.getStagePoint(), c));
    }

    private void putOnGrid(Map<Point, List<TransformationComponent>> gridMap,
                           Point point,
                           TransformationComponent component) {
        gridMap.computeIfAbsent(point, c -> new LinkedList<>()).add(component);
    }

    private Orientation rotate(Orientation orientation, Movement movement) {
        switch (orientation) {
        case NORTH:
            return movement == Movement.LEFT ? Orientation.WEST : Orientation.EAST;
        case EAST:
            return movement == Movement.LEFT ? Orientation.NORTH : Orientation.SOUTH;
        case WEST:
            return movement == Movement.LEFT ? Orientation.SOUTH : Orientation.NORTH;
        case SOUTH:
            return movement == Movement.LEFT ? Orientation.EAST : Orientation.WEST;
        }

        throw new IllegalArgumentException("Could not rotate");
    }

    private Point translateForward(Point currentPoint, Orientation orientation) {
        int x = currentPoint.getX();
        int y = currentPoint.getY();
        Point stagePoint = null;

        switch (orientation) {
        case NORTH:
            stagePoint = new Point(x, y + 1);
            break;
        case EAST:
            stagePoint = new Point(x + 1, y);
            break;
        case WEST:
            stagePoint = new Point(x - 1, y);
            break;
        case SOUTH:
            stagePoint = new Point(x, y - 1);
            break;
        default:
            stagePoint = new Point();
        }

        return grid.isInside(stagePoint) && !hasHeadCollision(currentPoint, orientation)
                ? stagePoint : currentPoint;
    }

    private boolean hasHeadCollision(Point currentPoint, Orientation orientation) {
        List<TransformationComponent> otherComponents
                = grid.getGridMap().getOrDefault(currentPoint, Collections.emptyList());

        if (otherComponents.isEmpty()) {
            return false;
        }

        TransformationComponent otherComponent = otherComponents.get(0);
        Orientation otherOrientation = otherComponent.getOrientation();

        switch (orientation) {
        case NORTH:
            return otherOrientation == Orientation.SOUTH;
        case EAST:
            return otherOrientation == Orientation.WEST;
        case WEST:
            return otherOrientation == Orientation.EAST;
        case SOUTH:
            return otherOrientation == Orientation.NORTH;
        }

        return false;
    }

    private void resolveCollisions() {
        List<Map.Entry<Point, List<TransformationComponent>>> collisions
                = grid.getGridMap()
                      .entrySet()
                      .parallelStream()
                      .filter(e -> e.getValue().size() > 1)
                      .collect(Collectors.toList());

        for (var collision : collisions) {
            resolveCollisions(collision.getValue());
        }
    }

    private void resolveCollisions(Collection<TransformationComponent> components) {
        if (components.size() < 2) {
            return;
        }

        for (Iterator<TransformationComponent> iterator = components.iterator(); iterator.hasNext(); ) {
            TransformationComponent component = iterator.next();
            Point currentPoint = component.getCurrentPoint();

            if (!component.isTranslated()) {
                continue;
            }

            iterator.remove();
            component.setStagePoint(currentPoint);

            Collection<TransformationComponent> otherComponents
                    = grid.getGridMap().computeIfAbsent(currentPoint, c -> new LinkedList<>());

            otherComponents.add(component);
            resolveCollisions(otherComponents);
        }
    }

    private void flip() {
        grid.getGridMap()
            .entrySet()
            .parallelStream()
            .map(Map.Entry::getValue)
            .flatMap(Collection::parallelStream)
            .forEach(TransformationComponent::flip);
    }
}