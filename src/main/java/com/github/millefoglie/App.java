package com.github.millefoglie;

import com.github.millefoglie.component.ComponentManager;
import com.github.millefoglie.component.DefaultComponentManager;
import com.github.millefoglie.engine.GameEngine;
import com.github.millefoglie.engine.ScenarioFileGameEngine;
import com.github.millefoglie.entity.DefaultEntityManager;
import com.github.millefoglie.entity.EntityManager;
import com.github.millefoglie.event.DefaultEventBus;
import com.github.millefoglie.event.EventBus;
import com.github.millefoglie.system.DefaultGameSystemManager;
import com.github.millefoglie.system.GameSystemManager;

import java.nio.file.Paths;

public class App {

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : "scenario.txt";
        ApplicationContext appCtx = ApplicationContext.getInstance();

        appCtx.registerBean(EntityManager.class, new DefaultEntityManager());
        appCtx.registerBean(ComponentManager.class, new DefaultComponentManager());
        appCtx.registerBean(GameSystemManager.class, new DefaultGameSystemManager());
        appCtx.registerBean(EventBus.class, new DefaultEventBus());

        GameEngine engine = new ScenarioFileGameEngine(Paths.get(filename));
        engine.start();
    }
}
