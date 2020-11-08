package com.github.millefoglie.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public abstract class AbstractGameEngine implements GameEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private volatile boolean running;

    @Override
    public final void start() {
        init();

        running = true;

        LOGGER.debug("Game started");

        while (running) {
            loop();
        }
    }

    @Override
    public final void stop() {
        running = false;

        LOGGER.debug("Game stopped");
        destroy();
    }
}
