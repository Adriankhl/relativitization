package relativitization.universe.mechanisms.defaults.dilated.research

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.components.playerScienceData
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class DiscoverKnowledgeTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(
            GenerateUniverseMethodCollection.generate(
                GenerateSettings(
                    generateMethod = TestingFixedMinimalGenerate.name(),
                    universeSettings = MutableUniverseSettings(
                        randomSeed = 100L,
                    )
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