package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.grid.Orientation;
import com.github.millefoglie.grid.Point;

import java.util.Objects;

public class TransformationComponent extends AbstractComponent {
    private Point currentPoint = new Point();
    private Point stagePoint;
    private Orientation orientation;

    public TransformationComponent(Entity entity) {
        super(entity);
    }

    public Point getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Point currentPoint) {
        this.currentPoint = currentPoint;

        if (stagePoint == null) {
            stagePoint = currentPoint;
        }
    }

    public Point getStagePoint() {
        return stagePoint;
    }

    public void setStagePoint(Point stagePoint) {
        this.stagePoint = stagePoint;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
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
