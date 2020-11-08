package com.github.millefoglie.engine;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.component.ComponentManager;
import com.github.millefoglie.component.SchedulerComponent;
import com.github.millefoglie.component.TransformationComponent;
import com.github.millefoglie.entity.Entity;
import com.github.millefoglie.entity.EntityManager;
import com.github.millefoglie.entity.EntityType;
import com.github.millefoglie.grid.Grid;
import com.github.millefoglie.grid.Movement;
import com.github.millefoglie.grid.Orientation;
import com.github.millefoglie.grid.Point;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

class ScenarioParser {
    private final Path scenarioPath;
    private final ApplicationContext appCtx = ApplicationContext.getInstance();
    private final EntityManager entityManager;
    private final ComponentManager componentManager;

    public ScenarioParser(Path scenarioPath) {
        this.scenarioPath = scenarioPath;
        entityManager = appCtx.getBean(EntityManager.class);
        componentManager = appCtx.getBean(ComponentManager.class);
    }

    void parse() {
        try {
            List<String> lines = Files.readAllLines(scenarioPath);

            parseGrid(lines.get(0));

            for (int i = 1; i < lines.size(); i += 2) {
                parseMower(lines.get(i), lines.get(i + 1));
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not parse scenario");
        }
    }

    void parseGrid(String line) {
        Scanner scanner = new Scanner(line);
        int width = scanner.nextInt();
        int height = scanner.nextInt();

        appCtx.registerBean(Grid.class, new Grid(width, height));
        scanner.close();
    }

    void parseMower(String positionLine, String movementLine) {
        Scanner positionScanner = new Scanner(positionLine);
        int x = positionScanner.nextInt();
        int y = positionScanner.nextInt();
        Orientation orientation = Orientation.from(positionScanner.next());

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

        positionScanner.close();
    }
}
