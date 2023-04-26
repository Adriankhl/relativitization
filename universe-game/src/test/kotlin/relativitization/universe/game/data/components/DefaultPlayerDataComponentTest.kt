package relativitization.universe.game.data.components

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.components.keyM
import relativitization.universe.core.data.serializer.DataSerializer
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.test.Test

internal class DefaultPlayerDataComponentTest {
    @Test
    fun serializationTest() {
        val mutablePlayerData = MutablePlayerData(1)
        MutableDefaultPlayerDataComponent::class.sealedSubclasses.forEach {
            mutablePlayerData.playerInternalData.playerDataComponentMap.put(it.createInstance())
        }
        val playerData: PlayerData = DataSerializer.copy(mutablePlayerData)

        val mutablePlayerData1: MutablePlayerData = DataSerializer.copy(playerData)

        assert(mutablePlayerData == mutablePlayerData1)
    }

    @Test
    fun sealedTest() {
        val l1: List<KClass<*>> = DefaultPlayerDataComponent::class.sealedSubclasses
        val l2: List<KClass<*>> = MutableDefaultPlayerDataComponent::class.sealedSubclasses

        assert(l1.size > 2)

        assert(l1.size == l2.size)

        l2.forEach { element2 ->
            assert(l1.any { element2.simpleName!!.drop(7) == it.simpleName })
        }
    }

    @Test
    fun componentListTest() {
        val l1: List<DefaultPlayerDataComponent> = DataSerializer.copy(
            MutableDefaultPlayerDataComponent::class
                .sealedSubclasses.sortedBy {
                    it.keyM()
                }.map {
                    it.createInstance()
                }
        )
        val l2: List<DefaultPlayerDataComponent> = DefaultPlayerDataComponent.createComponentList()

        assert(l1 == l2)

        val l3: List<MutableDefaultPlayerDataComponent> = MutableDefaultPlayerDataComponent::class
            .sealedSubclasses.sortedBy {
                it.keyM()
            }.map {
                it.createInstance()
            }

        val l4: List<MutableDefaultPlayerDataComponent> = MutableDefaultPlayerDataComponent
            .createComponentList()
        assert(l3 == l4)
    }
}