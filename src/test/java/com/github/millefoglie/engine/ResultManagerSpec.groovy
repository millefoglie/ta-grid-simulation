package com.github.millefoglie.engine

import com.github.millefoglie.ApplicationContext
import com.github.millefoglie.component.ComponentManager
import com.github.millefoglie.component.DefaultComponentManager
import com.github.millefoglie.component.TransformationComponent
import com.github.millefoglie.entity.DefaultEntityManager
import com.github.millefoglie.entity.EntityManager
import com.github.millefoglie.entity.EntityType
import com.github.millefoglie.grid.Orientation
import com.github.millefoglie.grid.Point
import spock.lang.Shared
import spock.lang.Specification

class ResultManagerSpec extends Specification {
    @Shared ApplicationContext appCtx = ApplicationContext.getInstance()

    def setup() {
        appCtx.registerBean(ComponentManager, new DefaultComponentManager())
        appCtx.registerBean(EntityManager, new DefaultEntityManager())
    }

    def "Print formatted results"() {
        given:
        def entityManager = appCtx.getBean(EntityManager)
        def entity1 = entityManager.createEntity(EntityType.MOWER)
        def entity2 = entityManager.createEntity(EntityType.MOWER)

        def componentManager = appCtx.getBean(ComponentManager)
        def component1 = componentManager.createComponent(entity1, TransformationComponent)
        component1.setCurrentPoint(new Point(1, 2))
        component1.setOrientation(Orientation.NORTH)
        def component2 = componentManager.createComponent(entity2, TransformationComponent)
        component2.setCurrentPoint(new Point(3, 4))
        component2.setOrientation(Orientation.EAST)

        ResultManager resultManager = new ResultManager()
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream()
        PrintStream printStream = new PrintStream(outputStream)

        when:
        resultManager.print(printStream)
        def results = new String(outputStream.toByteArray())
        printStream.close()

        then:
        results == "1 2 N\n3 4 E\n"
    }
}
