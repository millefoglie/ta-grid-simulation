package com.github.millefoglie.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Path;

public class ScenarioFileGameEngine extends AbstractGameEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final Path scenarioPath;

    public ScenarioFileGameEngine(Path scenarioPath) {
        this.scenarioPath = scenarioPath;
    }

    @Override
    public void init() {
        if ((scenarioPath == null) || !scenarioPath.toFile().exists()) {
            throw new IllegalStateException("No scenario file specified");
        }

        ScenarioParser scenarioParser = new ScenarioParser(scenarioPath);
        scenarioParser.parse();
        LOGGER.debug("Game initialized");
    }

    @Override
    public void loop() {
        LOGGER.info("Hello, world!");
        stop();
    }

    @Override
    public void destroy() {
        LOGGER.debug("Game over");
    }
}
