package com.github.millefoglie.grid;

import com.github.millefoglie.component.TransformationComponent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Grid {
    private final int width;
    private final int height;

    private final Map<Point, List<TransformationComponent>> gridMap = new ConcurrentHashMap<>();

    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Map<Point, List<TransformationComponent>> getGridMap() {
        return gridMap;
    }

    public boolean isInside(Point point) {
        int x = point.getX();
        int y = point.getY();

        return (x >= 0) && (x <= width) && (y >= 0) && (y <= height);
    }
}
