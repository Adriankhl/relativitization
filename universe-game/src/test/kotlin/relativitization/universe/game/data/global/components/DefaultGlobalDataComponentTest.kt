package relativitization.universe.game.data.global.components

import org.junit.jupiter.api.Test
import relativitization.universe.core.data.global.components.keyI
import relativitization.universe.core.data.global.components.keyM
import kotlin.reflect.full.createInstance

internal class DefaultGlobalDataComponentTest {
    @Test
    fun componentListTest() {
        val l1: List<DefaultGlobalDataComponent> = DefaultGlobalDataComponent::class
            .sealedSubclasses.sortedBy {
                it.keyI()
            }.map {
                it.createInstance()
            }

        val l2: List<DefaultGlobalDataComponent> = DefaultGlobalDataComponent.createComponentList()

        assert(l1 == l2)

        val l3: List<MutableDefaultGlobalDataComponent> = MutableDefaultGlobalDataComponent::class
            .sealedSubclasses.sortedBy {
                it.keyM()
            }.map {
                it.createInstance()
            }

        val l4: List<MutableDefaultGlobalDataComponent> = MutableDefaultGlobalDataComponent
            .createComponentList()
        assert(l3 == l4)
    }
}