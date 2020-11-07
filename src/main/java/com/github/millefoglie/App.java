package com.github.millefoglie;

import com.github.millefoglie.engine.GameEngine;
import com.github.millefoglie.engine.ScenarioFileGameEngine;

public class App {

    public static void main(String[] args) {
        GameEngine engine = new ScenarioFileGameEngine(null);
        engine.start();
    }
}
