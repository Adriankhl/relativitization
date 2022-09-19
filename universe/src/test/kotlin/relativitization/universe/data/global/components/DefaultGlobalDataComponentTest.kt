package relativitization.universe.data.global.components

import org.junit.jupiter.api.Test
import kotlin.reflect.full.createInstance

internal class DefaultGlobalDataComponentTest {
    @Test
    fun componentListTest() {
        val l1: List<DefaultGlobalDataComponent> = DefaultGlobalDataComponent::class
            .sealedSubclasses.sortedBy {
                it.name()
            }.map {
                it.createInstance()
            }

        val l2: List<DefaultGlobalDataComponent> = DefaultGlobalDataComponent.createComponentList()

        assert(l1 == l2)

        val l3: List<MutableDefaultGlobalDataComponent> = MutableDefaultGlobalDataComponent::class
            .sealedSubclasses.sortedBy {
                it.name()
            }.map {
                it.createInstance()
            }

        val l4: List<MutableDefaultGlobalDataComponent> = MutableDefaultGlobalDataComponent
            .createComponentList()
        assert(l3 == l4)
    }
}