package com.github.millefoglie.gridsim.system;

import com.github.millefoglie.gridsim.ApplicationContext;
import com.github.millefoglie.gridsim.component.ComponentManager;
import com.github.millefoglie.gridsim.component.TransformationComponent;
import com.github.millefoglie.gridsim.entity.Entity;
import com.github.millefoglie.gridsim.entity.EntityManager;
import com.github.millefoglie.gridsim.entity.EntityType;
import com.github.millefoglie.gridsim.event.EventBus;
import com.github.millefoglie.gridsim.event.GameOverEvent;
import com.github.millefoglie.gridsim.event.TransformationRequestedEvent;
import com.github.millefoglie.gridsim.exception.GameException;
import com.github.millefoglie.gridsim.grid.Grid;
import com.github.millefoglie.gridsim.grid.Movement;
import com.github.millefoglie.gridsim.grid.Orientation;
import com.github.millefoglie.gridsim.grid.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * System for managing entity positioning on the grid, including movement and collision detection
 */
public class TransformationSystem implements GameSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final ConcurrentLinkedDeque<TransformationComponent> EMPTY_CLD = new ConcurrentLinkedDeque<>();

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

        // if nothing happens, the game is over
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
        events.parallelStream().forEach(this::updatePosition);
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
        Map<Point, ConcurrentLinkedDeque<TransformationComponent>> gridMap = grid.getGridMap();

        gridMap.clear();
        componentManager.findAllByClass(TransformationComponent.class)
                        .parallelStream()
                        .forEach(c -> putOnGrid(gridMap, c.getStagePoint(), c));
    }

    private void putOnGrid(Map<Point, ConcurrentLinkedDeque<TransformationComponent>> gridMap,
                           Point point,
                           TransformationComponent component) {
        gridMap.computeIfAbsent(point, c -> new ConcurrentLinkedDeque<>()).add(component);
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

        throw GameException.couldNotTransform();
    }

    private Point translateForward(Point currentPoint, Orientation orientation) {
        int x = currentPoint.getX();
        int y = currentPoint.getY();
        Point stagePoint;

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
            throw GameException.couldNotTransform();
        }

        return grid.isInside(stagePoint) ? stagePoint : currentPoint;
    }

    // Check if two entities move toward each other and swap their positions
    private boolean hasHeadCollision(Point currentPoint, Orientation orientation) {
        ConcurrentLinkedDeque<TransformationComponent> otherComponents
                = grid.getGridMap().getOrDefault(currentPoint, EMPTY_CLD);

        if (otherComponents.isEmpty()) {
            return false;
        }

        TransformationComponent otherComponent = otherComponents.getFirst();
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

        throw GameException.couldNotTransform();
    }

    private void resolveCollisions() {
        List<Map.Entry<Point, ConcurrentLinkedDeque<TransformationComponent>>> collisions
                = grid.getGridMap()
                      .entrySet()
                      .parallelStream()
                      .filter(this::hasCollision)
                      .collect(Collectors.toList());

        for (var collision : collisions) {
            resolveCollisions(collision.getValue());
        }
    }

    private boolean hasCollision(Map.Entry<Point, ConcurrentLinkedDeque<TransformationComponent>> e) {
        return moreThanTwoComponentsAtPoint(e) || hasHeadCollision(e);
    }

    private boolean moreThanTwoComponentsAtPoint(Map.Entry<Point, ConcurrentLinkedDeque<TransformationComponent>> e) {
        return e.getValue().size() > 1;
    }

    private boolean hasHeadCollision(Map.Entry<Point, ConcurrentLinkedDeque<TransformationComponent>> e) {
        if (e.getValue().size() != 1) {
            return false;
        }

        TransformationComponent component = e.getValue().getFirst();
        return hasHeadCollision(component.getCurrentPoint(), component.getOrientation());
    }

    private void resolveCollisions(ConcurrentLinkedDeque<TransformationComponent> components) {
        if (components.size() == 0) {
            return;
        } else if (components.size() == 1) {
            TransformationComponent component = components.getFirst();

            if (!hasHeadCollision(component.getCurrentPoint(), component.getOrientation())) {
                return;
            }

            ConcurrentLinkedDeque<TransformationComponent> otherComponents = revertTranslation(component);

            if (otherComponents.isEmpty()) {
                return;
            }

            resolveCollisions(otherComponents);
        }

        for (Iterator<TransformationComponent> iterator = components.iterator(); iterator.hasNext(); ) {
            TransformationComponent component = iterator.next();
            ConcurrentLinkedDeque<TransformationComponent> otherComponents = revertTranslation(component);

            iterator.remove();

            if (otherComponents.isEmpty()) {
                continue;
            }

            resolveCollisions(otherComponents);
        }
    }

    // returns list of components affected by reversion
    private ConcurrentLinkedDeque<TransformationComponent> revertTranslation(TransformationComponent component) {
        if (!component.isTranslated()) {
            return EMPTY_CLD;
        }

        Point currentPoint = component.getCurrentPoint();

        component.setStagePoint(currentPoint);

        ConcurrentLinkedDeque<TransformationComponent> otherComponents
                = grid.getGridMap().computeIfAbsent(currentPoint, c -> new ConcurrentLinkedDeque<>());

        otherComponents.add(component);
        return otherComponents;
    }

    // flip double buffer (commit staged position changes)
    private void flip() {
        grid.getGridMap()
            .entrySet()
            .parallelStream()
            .map(Map.Entry::getValue)
            .flatMap(Collection::parallelStream)
            .forEach(TransformationComponent::flip);
    }
}
