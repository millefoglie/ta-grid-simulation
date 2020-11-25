package com.github.millefoglie.gridsim.system

import com.github.millefoglie.gridsim.ApplicationContext
import com.github.millefoglie.gridsim.component.ComponentManager
import com.github.millefoglie.gridsim.component.DefaultComponentManager
import com.github.millefoglie.gridsim.component.SchedulerComponent
import com.github.millefoglie.gridsim.entity.DefaultEntityManager
import com.github.millefoglie.gridsim.entity.EntityManager
import com.github.millefoglie.gridsim.entity.EntityType
import com.github.millefoglie.gridsim.event.DefaultEventBus
import com.github.millefoglie.gridsim.event.EventBus
import com.github.millefoglie.gridsim.event.TransformationRequestedEvent
import com.github.millefoglie.gridsim.grid.Grid
import com.github.millefoglie.gridsim.grid.Movement
import spock.lang.Shared
import spock.lang.Specification

class SchedulerSystemSpec extends Specification {
    @Shared ApplicationContext appCtx = ApplicationContext.getInstance()

    def setup() {
        appCtx.registerBean(EventBus, new DefaultEventBus())
        appCtx.registerBean(ComponentManager, new DefaultComponentManager())
        appCtx.registerBean(Grid, new Grid(4, 3))
        appCtx.registerBean(EntityManager, new DefaultEntityManager())
    }

    def "Scheduled movements are registered as events"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity = entityManager.createEntity(EntityType.MOWER)
        def componentManager = appCtx.getBean(ComponentManager)

        def component = componentManager.createComponent(entity, SchedulerComponent)
        component.offerMovement(Movement.FORWARD)
        component.offerMovement(Movement.LEFT)
        component.offerMovement(Movement.RIGHT)

        def eventBus = appCtx.getBean(EventBus)
        def sut = new SchedulerSystem()

        when:
        sut.update()
        def round1Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        sut.update()
        def round2Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        sut.update()
        def round3Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        then:
        verifyAll {
            round1Events.size() == 1
            round2Events.size() == 1
            round3Events.size() == 1
        }

        and:
        verifyAll {it ->
            round1Events[0].entity == entity
            round1Events[0].movement == Movement.FORWARD
            round2Events[0].entity == entity
            round2Events[0].movement == Movement.LEFT
            round3Events[0].entity == entity
            round3Events[0].movement == Movement.RIGHT
        }
    }

    def "Event bus has correct scheduler event count"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity1 = entityManager.createEntity(EntityType.MOWER)
        def entity2 = entityManager.createEntity(EntityType.MOWER)
        def componentManager = appCtx.getBean(ComponentManager)

        def component1 = componentManager.createComponent(entity1, SchedulerComponent)
        component1.offerMovement(Movement.FORWARD)
        component1.offerMovement(Movement.LEFT)

        def component2 = componentManager.createComponent(entity2, SchedulerComponent)
        component2.offerMovement(Movement.RIGHT)

        def eventBus = appCtx.getBean(EventBus)
        def sut = new SchedulerSystem()

        when:
        sut.update()
        def round1Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        sut.update()
        def round2Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        sut.update()
        def round3Events = eventBus.findAllByClass(TransformationRequestedEvent)
        eventBus.clear()

        then:
        verifyAll {
            round1Events.size() == 2
            round2Events.size() == 1
            round3Events.size() == 0
        }
    }
}
