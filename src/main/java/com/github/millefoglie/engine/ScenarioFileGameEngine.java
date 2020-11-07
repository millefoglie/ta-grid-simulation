package com.github.millefoglie.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.invoke.MethodHandles;

public class ScenarioFileGameEngine extends AbstractGameEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final File scenarioFile;

    public ScenarioFileGameEngine(File scenarioFile) {
        this.scenarioFile = scenarioFile;
    }

    @Override
    public void init() {
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
