package com.github.millefoglie.gridsim.engine

import com.github.millefoglie.gridsim.ApplicationContext
import com.github.millefoglie.gridsim.component.ComponentManager
import com.github.millefoglie.gridsim.component.DefaultComponentManager
import com.github.millefoglie.gridsim.component.SchedulerComponent
import com.github.millefoglie.gridsim.component.TransformationComponent
import com.github.millefoglie.gridsim.entity.DefaultEntityManager
import com.github.millefoglie.gridsim.entity.EntityManager
import com.github.millefoglie.gridsim.entity.EntityType
import com.github.millefoglie.gridsim.grid.Grid
import com.github.millefoglie.gridsim.grid.Movement
import com.github.millefoglie.gridsim.grid.Orientation
import spock.lang.Shared
import spock.lang.Specification

class ScenarioParserSpec extends Specification {
    @Shared ApplicationContext appCtx = ApplicationContext.getInstance()

    def setup() {
        appCtx.registerBean(ComponentManager, new DefaultComponentManager())
        appCtx.registerBean(EntityManager, new DefaultEntityManager())
    }

    def "Parse lines for two entities"() {
        given:
        List<String> lines = ["4 5", "1 2 N", "LRF", "2 3 E", "FRL"]
        def entityManager = appCtx.getBean(EntityManager)
        def scenarioParser = new ScenarioParser()

        when:
        scenarioParser.parse(lines)

        then:
        Grid grid = appCtx.getBean(Grid)
        grid != null

        and:
        verifyAll(grid) {
            width == 4
            height == 5
        }

        and:
        def entities = entityManager.findAllByType(EntityType.MOWER)
        entities.size() == 2
        verifyAll {
            entities[0].getComponent(TransformationComponent).currentPoint.x == 1
            entities[0].getComponent(TransformationComponent).currentPoint.y == 2
            entities[0].getComponent(TransformationComponent).orientation == Orientation.NORTH
            entities[0].getComponent(SchedulerComponent).pollMovement() == Movement.LEFT
            entities[1].getComponent(TransformationComponent).currentPoint.x == 2
            entities[1].getComponent(TransformationComponent).currentPoint.y == 3
            entities[1].getComponent(TransformationComponent).orientation == Orientation.EAST
            entities[1].getComponent(SchedulerComponent).pollMovement() == Movement.FORWARD
        }
    }
}
