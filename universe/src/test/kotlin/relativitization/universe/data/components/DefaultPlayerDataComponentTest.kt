package relativitization.universe.data.components

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.serializer.DataSerializer
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
}