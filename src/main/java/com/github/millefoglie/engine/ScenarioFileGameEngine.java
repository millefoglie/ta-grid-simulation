package com.github.millefoglie.engine;

import com.github.millefoglie.ApplicationContext;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.event.GameOverEvent;
import com.github.millefoglie.system.GameSystemManager;
import com.github.millefoglie.system.SchedulerSystem;
import com.github.millefoglie.system.TransformationSystem;
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
        systemManager.registerSystem(new TransformationSystem());
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

    @Override
    public void destroy() {
        ResultManager resultManager = new ResultManager();

        resultManager.print();
        LOGGER.debug("Game destroyed");
    }
}
