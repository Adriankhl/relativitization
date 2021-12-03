package relativitization.universe.mechanisms.defaults.dilated.research

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.generate.method.GenerateSettings
import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class DiscoverKnowledgeTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(GenerateUniverseMethodCollection.generate(GenerateSettings()), ".")

        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(!view7.get(1).playerInternalData.playerScienceData().doneBasicResearchProjectList.any {
            it.basicResearchId == 2
        })

        assert(!view7.get(1).playerInternalData.playerScienceData().doneAppliedResearchProjectList.any {
            it.appliedResearchId == 2
        })


        runBlocking {
            universe.postProcessUniverse(
                mapOf(),
                mapOf()
            )
            universe.preProcessUniverse()
        }

        val view8: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        // There is a small probability to fail, since research is not guarantee to success
        assert(view8.get(1).playerInternalData.playerScienceData().doneBasicResearchProjectList.any {
            it.basicResearchId == 2
        })
        assert(view8.get(1).playerInternalData.playerScienceData().doneAppliedResearchProjectList.any {
            it.appliedResearchId == 2
        })
    }
}