package relativitization.universe.data.components

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.serializer.DataSerializer
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
}