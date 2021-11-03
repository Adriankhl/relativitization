package relativitization.universe.mechanisms.research

import kotlinx.coroutines.runBlocking
import relativitization.universe.Universe
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class DiscoverKnowledgeTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")

        val view7: UniverseData3DAtPlayer = universe.getUniverse3DViewAtPlayer(1)

        assert(!view7.get(1).playerInternalData.playerScienceData().doneBasicResearchProjectList.any {
            it.basicResearchId == 2
        })


        runBlocking {
            universe.postProcessUniverse(mapOf(
                1 to listOf()),
                mapOf(
                    2 to listOf(),
                    3 to listOf(),
                    4 to listOf()
                )
            )
            universe.preProcessUniverse()
        }
    }
}