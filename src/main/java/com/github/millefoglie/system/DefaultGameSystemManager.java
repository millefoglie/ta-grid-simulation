package com.github.millefoglie.system;

import java.util.ArrayList;
import java.util.List;

public class DefaultGameSystemManager implements GameSystemManager {
    private final List<GameSystem> gameSystems = new ArrayList<>();

    @Override
    public void register(GameSystem system) {
        gameSystems.add(system);
    }

    @Override
    public void updateAll() {
        for (GameSystem system : gameSystems) {
            system.update();
        }
    }
}
