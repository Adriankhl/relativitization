package relativitization.universe.game.data.commands

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class GrantIndependenceCommandTest {
    @Test
    fun fixedMinimalTest() {
        GameUniverseInitializer.initialize()

        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                    universeSettings = MutableUniverseSettings(
                        commandCollectionName = DefaultCommandAvailability.name(),
                        mechanismCollectionName = DefaultMechanismLists.name(),
                        globalMechanismCollectionName = DefaultGlobalMechanismList.name(),
                    ),
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