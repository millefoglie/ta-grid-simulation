package com.github.millefoglie.engine;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.system.GameSystemManager;
import com.github.millefoglie.system.SchedulerSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

public class ScenarioFileGameEngine extends AbstractGameEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Path scenarioPath;
    private final GameSystemManager systemManager;
    private final EventBus eventBus;

    public ScenarioFileGameEngine(Path scenarioPath) {
        this.scenarioPath = scenarioPath;
        ApplicationContext appCtx = ApplicationContext.getInstance();
        this.systemManager = appCtx.getBean(GameSystemManager.class);
        this.eventBus = appCtx.getBean(EventBus.class);
    }

    @Override
    public void init() {
        if ((scenarioPath == null) || !scenarioPath.toFile().exists()) {
            throw new IllegalStateException("No scenario file specified");
        }

        ScenarioParser scenarioParser = new ScenarioParser(scenarioPath);
        scenarioParser.parse();

        systemManager.registerSystem(new SchedulerSystem());
        LOGGER.debug("Game initialized");
    }

    @Override
    public void loop() {
        eventBus.clear();
        systemManager.updateAll();

        // TODO implement game over condition
        stop();
    }

    @Override
    public void destroy() {
        LOGGER.debug("Game over");
    }
}
