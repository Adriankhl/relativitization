package relativitization.universe.mechanisms.defaults.combat

import relativitization.universe.Universe
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class AutoCombatTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")

        val view7At6 = universe.getUniverse3DViewAtPlayer(6)

        assert(view7At6.getCurrentPlayerData().isTopLeader())
        assert(view7At6.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())
    }
}