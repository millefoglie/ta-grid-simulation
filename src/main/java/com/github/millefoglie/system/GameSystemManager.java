package com.github.millefoglie.system;

public interface GameSystemManager {
    void registerSystem(GameSystem system);
    void updateAll();
}
