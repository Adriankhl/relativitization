package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.game.Universe
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.generate.GenerateSettings
import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class GrantIndependenceCommandTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                )
            )
        )
        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view1.get(1).playerInternalData.subordinateIdSet == setOf(2))
        assert(view1.get(1).playerInternalData.directSubordinateIdSet == setOf(2))
        assert(!view1.get(2).isTopLeader())
        assert(view1.get(2).topLeaderId() == 1)

        runBlocking {
            universe.postProcessUniverse(
                mapOf(
                    1 to listOf(
                        GrantIndependenceCommand(
                            toId = 2,
                        )
                    )
                ),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(view2.get(1).playerInternalData.subordinateIdSet.isEmpty())
        assert(view2.get(1).playerInternalData.directSubordinateIdSet.isEmpty())
        assert(view2.get(2).isTopLeader())
    }
}