package com.github.millefoglie;

import com.github.millefoglie.component.ComponentManager;
import com.github.millefoglie.component.DefaultComponentManager;
import com.github.millefoglie.engine.GameEngine;
import com.github.millefoglie.engine.ScenarioFileGameEngine;
import com.github.millefoglie.entity.DefaultEntityManager;
import com.github.millefoglie.entity.EntityManager;

import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "scenario.txt";
        ApplicationContext appCtx = ApplicationContext.getInstance();

        appCtx.registerBean(EntityManager.class, new DefaultEntityManager());
        appCtx.registerBean(ComponentManager.class, new DefaultComponentManager());

        GameEngine engine = new ScenarioFileGameEngine(Paths.get(filename));
        engine.start();
    }
}
