package relativitization.universe.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import kotlin.test.Test

internal class GrantIndependenceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimal.name(),
                )
            ), "."
        )
        val view6: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view6.get(1).playerInternalData.subordinateIdSet == setOf(2))
        assert(view6.get(1).playerInternalData.directSubordinateIdSet == setOf(2))
        assert(!view6.get(2).isTopLeader())
        assert(view6.get(2).topLeaderId() == 1)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        GrantIndependenceCommand(
                            toId = 2,
                            fromId = 1,
                            fromInt4D = view6.getCurrentPlayerData().int4D,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view7.get(1).playerInternalData.subordinateIdSet.isEmpty())
        assert(view7.get(1).playerInternalData.directSubordinateIdSet.isEmpty())
        assert(view7.get(2).isTopLeader())
    }
}