package com.github.millefoglie.gridsim;

import com.github.millefoglie.gridsim.component.ComponentManager;
import com.github.millefoglie.gridsim.component.DefaultComponentManager;
import com.github.millefoglie.gridsim.engine.GameEngine;
import com.github.millefoglie.gridsim.engine.ScenarioFileGameEngine;
import com.github.millefoglie.gridsim.entity.DefaultEntityManager;
import com.github.millefoglie.gridsim.entity.EntityManager;
import com.github.millefoglie.gridsim.event.DefaultEventBus;
import com.github.millefoglie.gridsim.event.EventBus;
import com.github.millefoglie.gridsim.system.DefaultGameSystemManager;
import com.github.millefoglie.gridsim.system.GameSystemManager;

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
