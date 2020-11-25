package com.github.millefoglie.gridsim.engine;

import com.github.millefoglie.gridsim.ApplicationContext;
import com.github.millefoglie.gridsim.component.ComponentManager;
import com.github.millefoglie.gridsim.component.SchedulerComponent;
import com.github.millefoglie.gridsim.component.TransformationComponent;
import com.github.millefoglie.gridsim.entity.Entity;
import com.github.millefoglie.gridsim.entity.EntityManager;
import com.github.millefoglie.gridsim.entity.EntityType;
import com.github.millefoglie.gridsim.exception.FileReadException;
import com.github.millefoglie.gridsim.grid.Grid;
import com.github.millefoglie.gridsim.grid.Movement;
import com.github.millefoglie.gridsim.grid.Orientation;
import com.github.millefoglie.gridsim.grid.Point;

import java.util.List;
import java.util.Scanner;

class ScenarioParser {
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final EntityManager entityManager;
    private final ComponentManager componentManager;

    ScenarioParser() {
        entityManager = appCtx.getBean(EntityManager.class);
        componentManager = appCtx.getBean(ComponentManager.class);
    }

    void parse(List<String> lines) {
        try {
            parseGrid(lines.get(0));

            for (int i = 1; i < lines.size(); i += 2) {
                parseMower(lines.get(i), lines.get(i + 1));
            }
        } catch (Exception e) {
            throw FileReadException.couldNotParseScenarioFile();
        }
    }

    private void parseGrid(String line) {
        Scanner scanner = new Scanner(line);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        if (scanner.hasNext() || (width < 0) || (height < 0)) {
            scanner.close();
            throw FileReadException.couldNotParseScenarioFile();
        }

        scanner.close();
        appCtx.registerBean(Grid.class, new Grid(width, height));
    }

    private void parseMower(String positionLine, String movementLine) {
        Scanner positionScanner = new Scanner(positionLine);
        int x = positionScanner.nextInt();
        int y = positionScanner.nextInt();
        Orientation orientation = Orientation.from(positionScanner.next());

        if (positionScanner.hasNext()) {
            positionScanner.close();
            throw FileReadException.couldNotParseScenarioFile();
        }

        positionScanner.close();

        Entity mower = entityManager.createEntity(EntityType.MOWER);
        TransformationComponent transformationComponent
                = componentManager.createComponent(mower, TransformationComponent.class);

        transformationComponent.setCurrentPoint(new Point(x, y));
        transformationComponent.setOrientation(orientation);

        SchedulerComponent schedulerComponent
                = componentManager.createComponent(mower, SchedulerComponent.class);

        for (char ch : movementLine.toCharArray()) {
            schedulerComponent.offerMovement(Movement.from(ch));
        }
    }
}
