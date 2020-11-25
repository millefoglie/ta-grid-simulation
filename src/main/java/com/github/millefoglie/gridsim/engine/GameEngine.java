package com.github.millefoglie.gridsim.engine;

public interface GameEngine {
    void init();
    void start();
    void loop();
    void stop();
    void destroy();
}
