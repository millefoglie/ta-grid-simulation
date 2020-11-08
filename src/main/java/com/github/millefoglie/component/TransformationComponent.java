package com.github.millefoglie.component;

import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.grid.Orientation;

public class TransformationComponent extends AbstractComponent {
    private int x;
    private int y;
    private Orientation orientation;

    public TransformationComponent(Entity entity) {
        super(entity);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }
}
