package com.github.millefoglie.gridsim.system

import com.github.millefoglie.gridsim.ApplicationContext
import com.github.millefoglie.gridsim.component.ComponentManager
import com.github.millefoglie.gridsim.component.DefaultComponentManager
import com.github.millefoglie.gridsim.component.TransformationComponent
import com.github.millefoglie.gridsim.entity.DefaultEntityManager
import com.github.millefoglie.gridsim.entity.EntityManager
import com.github.millefoglie.gridsim.entity.EntityType
import com.github.millefoglie.gridsim.event.DefaultEventBus
import com.github.millefoglie.gridsim.event.EventBus
import com.github.millefoglie.gridsim.event.GameOverEvent
import com.github.millefoglie.gridsim.event.TransformationRequestedEvent
import com.github.millefoglie.gridsim.grid.Grid
import com.github.millefoglie.gridsim.grid.Movement
import com.github.millefoglie.gridsim.grid.Orientation
import com.github.millefoglie.gridsim.grid.Point
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class TransformationSystemSpec extends Specification {
    @Shared ApplicationContext appCtx = ApplicationContext.getInstance()

    def setup() {
        appCtx.registerBean(EventBus, new DefaultEventBus())
        appCtx.registerBean(ComponentManager, new DefaultComponentManager())
        appCtx.registerBean(Grid, new Grid(4, 3))
        appCtx.registerBean(EntityManager, new DefaultEntityManager())
    }

    def "Game over when no more moves scheduled"() {
        given:
        def sut = new TransformationSystem()
        def eventBus = appCtx.getBean(EventBus)

        when:
        sut.update()

        then:
        !eventBus.findAllByClass(GameOverEvent).isEmpty()
    }

    @Unroll
    def "#movement move from position (1 1 #orientation1) leads to position (#x2 #y2 #orientation2)"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity = entityManager.createEntity(EntityType.MOWER)
        def componentManager = appCtx.getBean(ComponentManager)
        def component = componentManager.createComponent(entity, TransformationComponent)
        component.setCurrentPoint(new Point(1, 1))
        component.setOrientation(orientation1)

        def eventBus = appCtx.getBean(EventBus)
        eventBus.register(new TransformationRequestedEvent(entity, movement))

        def sut = new TransformationSystem()

        when:
        sut.update()

        then:
        verifyAll {
            component.currentPoint.x == x2
            component.currentPoint.y == y2
            component.orientation == orientation2
        }

        where:
        orientation1      | movement         | x2 | y2 | orientation2
        Orientation.NORTH | Movement.FORWARD | 1  | 2  | Orientation.NORTH
        Orientation.NORTH | Movement.LEFT    | 1  | 1  | Orientation.WEST
        Orientation.NORTH | Movement.RIGHT   | 1  | 1  | Orientation.EAST
        Orientation.EAST  | Movement.FORWARD | 2  | 1  | Orientation.EAST
        Orientation.EAST  | Movement.LEFT    | 1  | 1  | Orientation.NORTH
        Orientation.EAST  | Movement.RIGHT   | 1  | 1  | Orientation.SOUTH
        Orientation.WEST  | Movement.FORWARD | 0  | 1  | Orientation.WEST
        Orientation.WEST  | Movement.LEFT    | 1  | 1  | Orientation.SOUTH
        Orientation.WEST  | Movement.RIGHT   | 1  | 1  | Orientation.NORTH
        Orientation.SOUTH | Movement.FORWARD | 1  | 0  | Orientation.SOUTH
        Orientation.SOUTH | Movement.LEFT    | 1  | 1  | Orientation.EAST
        Orientation.SOUTH | Movement.RIGHT   | 1  | 1  | Orientation.WEST
    }

    @Unroll
    def "Discard movement if exiting grid boundary from (#x #y #orientation)"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity = entityManager.createEntity(EntityType.MOWER)
        def componentManager = appCtx.getBean(ComponentManager)
        def component = componentManager.createComponent(entity, TransformationComponent)
        component.setCurrentPoint(new Point(x, y))
        component.setOrientation(orientation)

        def eventBus = appCtx.getBean(EventBus)
        eventBus.register(new TransformationRequestedEvent(entity, Movement.FORWARD))

        def sut = new TransformationSystem()

        when:
        sut.update()

        then:
        verifyAll {
            component.currentPoint.x == x
            component.currentPoint.y == y
            component.orientation == orientation
        }

        where:
        x | y | orientation
        0 | 1 | Orientation.WEST
        0 | 0 | Orientation.WEST
        0 | 0 | Orientation.SOUTH
        0 | 3 | Orientation.WEST
        0 | 3 | Orientation.NORTH
        1 | 3 | Orientation.NORTH
        4 | 3 | Orientation.NORTH
        4 | 3 | Orientation.EAST
        4 | 1 | Orientation.EAST
        4 | 0 | Orientation.EAST
        4 | 0 | Orientation.SOUTH
        1 | 0 | Orientation.SOUTH
    }

    // there's probably a better way to write this
    @Unroll
    def "Collisions: movements (#x11 #y11 #o11 #m11), (#x12 #y12 #o12 #m12), (#x13 #y13 #o13 #m13), result in (#x21 #y21 #o21), (#x22 #y22 #o22 #m22), (#x23 #y23 #o23)"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity1 = entityManager.createEntity(EntityType.MOWER)
        def entity2 = entityManager.createEntity(EntityType.MOWER)
        def entity3 = entityManager.createEntity(EntityType.MOWER)
        def componentManager = appCtx.getBean(ComponentManager)
        def component1 = componentManager.createComponent(entity1, TransformationComponent)
        def component2 = componentManager.createComponent(entity2, TransformationComponent)
        def component3 = componentManager.createComponent(entity3, TransformationComponent)
        component1.setCurrentPoint(new Point(x11, y11))
        component1.setOrientation(o11)
        component2.setCurrentPoint(new Point(x12, y12))
        component2.setOrientation(o12)
        component3.setCurrentPoint(new Point(x13, y13))
        component3.setOrientation(o13)

        def eventBus = appCtx.getBean(EventBus)
        eventBus.register(new TransformationRequestedEvent(entity1, m11))
        eventBus.register(new TransformationRequestedEvent(entity2, m12))
        eventBus.register(new TransformationRequestedEvent(entity3, m13))

        def sut = new TransformationSystem()

        when:
        sut.update()

        then:
        verifyAll {
            component1.currentPoint.x == x21
            component1.currentPoint.y == y21
            component1.orientation == o21
            component2.currentPoint.x == x22
            component2.currentPoint.y == y22
            component2.orientation == o22
            component3.currentPoint.x == x23
            component3.currentPoint.y == y23
            component3.orientation == o23
        }

        where:
        x11 | y11 | o11              | m11              | x12 | y12 | o12               | m12              | x13 | y13 | o13               | m13              | x21 | y21 | o21              | x22 | y22 | o22               | x23 | y23 | o23
        0   | 0   | Orientation.EAST | Movement.FORWARD | 1   | 0   | Orientation.EAST  | Movement.FORWARD | 2   | 0   | Orientation.EAST  | Movement.LEFT    | 0   | 0   | Orientation.EAST | 1   | 0   | Orientation.EAST  | 2   | 0   | Orientation.NORTH
        0   | 0   | Orientation.EAST | Movement.FORWARD | 1   | 0   | Orientation.EAST  | Movement.FORWARD | 2   | 0   | Orientation.SOUTH | Movement.FORWARD | 0   | 0   | Orientation.EAST | 1   | 0   | Orientation.EAST  | 2   | 0   | Orientation.SOUTH
        0   | 0   | Orientation.EAST | Movement.FORWARD | 1   | 0   | Orientation.EAST  | Movement.FORWARD | 2   | 0   | Orientation.NORTH | Movement.FORWARD | 1   | 0   | Orientation.EAST | 2   | 0   | Orientation.EAST  | 2   | 1   | Orientation.NORTH
        1   | 1   | Orientation.EAST | Movement.FORWARD | 2   | 2   | Orientation.SOUTH | Movement.FORWARD | 2   | 1   | Orientation.NORTH | Movement.RIGHT   | 1   | 1   | Orientation.EAST | 2   | 2   | Orientation.SOUTH | 2   | 1   | Orientation.EAST
        1   | 1   | Orientation.EAST | Movement.FORWARD | 2   | 1   | Orientation.WEST  | Movement.FORWARD | 3   | 1   | Orientation.NORTH | Movement.FORWARD | 1   | 1   | Orientation.EAST | 2   | 1   | Orientation.WEST  | 3   | 2   | Orientation.NORTH
    }
}
