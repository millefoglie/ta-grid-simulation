package com.github.millefoglie.engine;

public interface GameEngine {
    void init();
    void start();
    void loop();
    void stop();
    void destroy();
}
