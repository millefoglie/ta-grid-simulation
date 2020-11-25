package com.github.millefoglie.gridsim.system;

public interface GameSystemManager {
    void register(GameSystem system);
    void updateAll();
}
