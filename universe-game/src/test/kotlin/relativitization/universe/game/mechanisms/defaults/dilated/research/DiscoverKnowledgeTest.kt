package relativitization.universe.game.mechanisms.defaults.dilated.research

import kotlinx.coroutines.runBlocking
import relativitization.universe.core.Universe
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.components.playerScienceData
import relativitization.universe.core.generate.GenerateSettings
import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.GameUniverseInitializer
import relativitization.universe.game.data.commands.DefaultCommandAvailability
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import relativitization.universe.game.global.DefaultGlobalMechanismList
import relativitization.universe.game.mechanisms.DefaultMechanismLists
import kotlin.test.Test

internal class DiscoverKnowledgeTest {
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
                        randomSeed = 100L,
                    ),
                )
            )
        )

        val view1: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(
            !view1.get(1).playerInternalData.playerScienceData().doneBasicResearchProjectList.any {
                it.basicResearchId == 2
            }
        )

        assert(!view1.get(1).playerInternalData.playerScienceData().doneAppliedResearchProjectList.any {
            it.appliedResearchId == 2
        })


        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view2: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        // There is a small probability to fail, since research is not guarantee to success
        assert(view2.get(1).playerInternalData.playerScienceData().doneBasicResearchProjectList.any {
            it.basicResearchId == 2
        })
        assert(view2.get(1).playerInternalData.playerScienceData().doneAppliedResearchProjectList.any {
            it.appliedResearchId == 2
        })
    }
}