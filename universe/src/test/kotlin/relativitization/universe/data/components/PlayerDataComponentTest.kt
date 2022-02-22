package relativitization.universe.data.components

import kotlin.reflect.KClass
import kotlin.test.Test

internal class PlayerDataComponentTest {
    @Test
    fun sealedTest() {
        val l1: List<KClass<*>> = PlayerDataComponent::class.sealedSubclasses
        val l2: List<KClass<*>> = MutablePlayerDataComponent::class.sealedSubclasses

        assert(l1.size == l2.size)

        l2.forEach { element2 ->
            assert(l1.any { element2.simpleName!!.drop(7) == it.simpleName })
        }

    }
}