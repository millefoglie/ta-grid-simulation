package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.grid.Orientation;
import com.github.millefoglie.grid.Point;

import java.util.Objects;

/**
 * Component for managing entity positioning
 *
 * This component uses double buffering for staging next grid positions,
 * detecting collisions and rolling back any forbidden movements.
 */
public class TransformationComponent extends AbstractComponent {
    private Point currentPoint;
    private Point stagePoint;
    private Orientation orientation;

    public TransformationComponent(Entity entity) {
        super(entity);
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        Objects.requireNonNull(currentPoint);

        this.currentPoint = currentPoint;

        if (stagePoint == null) {
            stagePoint = currentPoint;
        }
    }

    public Point getStagePoint() {
        return stagePoint;
    }

    public void setStagePoint(Point stagePoint) {
        Objects.requireNonNull(stagePoint);
        this.stagePoint = stagePoint;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        Objects.requireNonNull(orientation);
        this.orientation = orientation;
    }

    public boolean isTranslated() {
        return !Objects.equals(currentPoint, stagePoint);
    }

    public void flip() {
        currentPoint = stagePoint;
    }

    @Override
    public String toString() {
        return "TransformationComponent{" +
                "currentPoint=" + currentPoint +
                ", stagePoint=" + stagePoint +
                ", orientation=" + orientation +
                "} " + super.toString();
    }
}
