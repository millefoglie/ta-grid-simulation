package com.github.millefoglie.grid;

import com.github.millefoglie.component.TransformationComponent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Grid {
    private final int width;
    private final int height;

    private final Map<Point, ConcurrentLinkedDeque<TransformationComponent>> gridMap = new ConcurrentHashMap<>();

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<Point, ConcurrentLinkedDeque<TransformationComponent>> getGridMap() {
        return gridMap;
    }

    public boolean isInside(Point point) {
        int x = point.getX();
        int y = point.getY();

        return (x >= 0) && (x <= width) && (y >= 0) && (y <= height);
    }
}
