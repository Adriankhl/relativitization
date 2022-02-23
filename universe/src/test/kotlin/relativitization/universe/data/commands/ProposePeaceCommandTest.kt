package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class ProposePeaceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            )
        )

        val view6At3 = universe.getUniverse3DViewAtPlayer(3)
        val view6At5 = universe.getUniverse3DViewAtPlayer(5)

        assert(view6At3.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())
        assert(view6At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    5 to listOf(
                        DeclareWarCommand(
                            toId = 3,
                            fromId = 5,
                            fromInt4D = view6At5.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view8At3 = universe.getUniverse3DViewAtPlayer(3)
        val view8At5 = universe.getUniverse3DViewAtPlayer(5)



        assert(view8At3.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())
        assert(
            view8At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.keys == setOf(3)
        )

        runBlocking {
            for (i in (1..2)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val view10At3 = universe.getUniverse3DViewAtPlayer(3)
        val view10At5 = universe.getUniverse3DViewAtPlayer(5)


        assert(
            view10At3.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.keys == setOf(5)
        )
        assert(
            view10At5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.keys == setOf(3)
        )

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    3 to listOf(
                        ProposePeaceCommand(
                            toId = 3,
                            fromId = 3,
                            fromInt4D = view10At3.getCurrentPlayerData().int4D,
                            targetPlayerId = 5
                        )
                    ),
                    5 to listOf(
                        ProposePeaceCommand(
                            toId = 5,
                            fromId = 5,
                            fromInt4D = view10At5.getCurrentPlayerData().int4D,
                            targetPlayerId = 3
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        runBlocking {
            for (i in (1..3)) {
                universe.postProcessUniverse(
                    mapOf(),
                    mapOf()
                )
                universe.preProcessUniverse()
            }
        }


        val viewFinalAt3 = universe.getUniverse3DViewAtPlayer(3)
        val viewFinalAt5 = universe.getUniverse3DViewAtPlayer(5)

        assert(viewFinalAt3.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())
        assert(viewFinalAt5.getCurrentPlayerData().playerInternalData.diplomacyData().warData.warStateMap.isEmpty())

    }
}