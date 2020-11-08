package com.github.millefoglie.engine;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.event.GameOverEvent;
import com.github.millefoglie.exception.FileReadException;
import com.github.millefoglie.system.GameSystemManager;
import com.github.millefoglie.system.SchedulerSystem;
import com.github.millefoglie.system.TransformationSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ScenarioFileGameEngine extends AbstractGameEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Path scenarioPath;
    private final GameSystemManager systemManager;
    private final EventBus eventBus;

    public ScenarioFileGameEngine(Path scenarioPath) {
        Objects.requireNonNull(scenarioPath);

        this.scenarioPath = scenarioPath;
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.systemManager = appCtx.getBean(GameSystemManager.class);
        this.eventBus = appCtx.getBean(EventBus.class);
    }

    @Override
    public void init() {
        if (!scenarioPath.toFile().exists()) {
            throw new IllegalStateException("Scenario file does not exist");
        }

        ScenarioParser scenarioParser = new ScenarioParser();

        try {
            scenarioParser.parse(Files.readAllLines(scenarioPath));
        } catch (Exception e) {
            throw FileReadException.couldNotParseScenarioFile();
        }

             systemManager.register(new SchedulerSystem());
             systemManager.register(new TransformationSystem());
             LOGGER.debug("Game initialized");
    }

    @Override
    public void loop() {
        LOGGER.trace("Turn started");
        eventBus.clear();
        systemManager.updateAll();
        LOGGER.trace("Turn finished");

        if (!eventBus.findAllByClass(GameOverEvent.class).isEmpty()) {
            stop();
            LOGGER.trace("Game over");
        }
    }

    // clean up is ignored since the app closes anyway after destroy
    @Override
    public void destroy() {
        ResultManager resultManager = new ResultManager();

        resultManager.print(System.out);
        LOGGER.debug("Game destroyed");
    }
}
