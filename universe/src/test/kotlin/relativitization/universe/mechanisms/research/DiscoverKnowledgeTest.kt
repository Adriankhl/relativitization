package relativitization.universe.mechanisms.research

import relativitization.universe.Universe
import relativitization.universe.generate.GenerateSettings
import relativitization.universe.generate.UniverseGenerationCollection
import kotlin.test.Test

internal class DiscoverKnowledgeTest {
    @Test
    fun fixedMinimalTest() {
        val universe = Universe(UniverseGenerationCollection.generate(GenerateSettings()), ".")
    }
}