package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class DeclareWarCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view6At5 = universe.getUniverse3DViewAtPlayer(5)
        val view6At6 = universe.getUniverse3DViewAtPlayer(6)

        assert(view6At5.getCurrentPlayerData().double4D.x == 1.5)
        assert(view6At6.getCurrentPlayerData().double4D.x == 1.4)
        assert(view6At5.getCurrentPlayerData().groupId != view6At6.getCurrentPlayerData().groupId)

        assert(view6At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())
        assert(view6At6.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 6,
                            fromId = 5,
                            fromInt4D = view6At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view7At5 = universe.getUniverse3DViewAtPlayer(5)
        val view7At6 = universe.getUniverse3DViewAtPlayer(6)


        assert(
            view7At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                6
            )
        )
        assert(view7At6.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())

        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }


        val view8At5 = universe.getUniverse3DViewAtPlayer(5)
        val view8At6 = universe.getUniverse3DViewAtPlayer(6)


        assert(
            view8At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                6
            )
        )
        assert(
            view8At6.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                5
            )
        )
    }
}