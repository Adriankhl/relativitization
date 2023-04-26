package relativitization.universe.game.generate.random.science


import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.game.data.global.components.MutableUniverseScienceData
import relativitization.universe.game.data.global.components.UniverseScienceData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.MutableAppliedResearchProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.MutableBasicResearchProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.MutableProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.MutableUniverseProjectGenerationData
import kotlin.random.Random
import kotlin.test.Test

internal class DefaultGenerateUniverseScienceDataTest {
    @Test
    fun testGenerateDefaultField() {
        val universeScienceData = MutableUniverseScienceData(
            universeProjectGenerationData = MutableUniverseProjectGenerationData(
                basicResearchProjectGenerationDataList = mutableListOf(
                    MutableBasicResearchProjectGenerationData(
                        basicResearchField = BasicResearchField.MATHEMATICS,
                        projectGenerationData = MutableProjectGenerationData(
                            centerX = 0.0,
                            centerY = 0.0,
                            range = 1.0,
                            weight = 1.0,
                        )
                    )
                ),
                appliedResearchProjectGenerationDataList = mutableListOf(
                   MutableAppliedResearchProjectGenerationData(
                       appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                       projectGenerationData = MutableProjectGenerationData(
                           centerX = 0.0,
                           centerY = 0.0,
                           range = 1.0,
                           weight = 1.0,
                       )
                   )
                )
            )
        )

        val newUniverseScienceData: UniverseScienceData =
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData = DataSerializer.copy(universeScienceData),
                numBasicResearchProjectGenerate = 10,
                numAppliedResearchProjectGenerate = 10,
                maxBasicReference = 10,
                maxAppliedReference = 10,
                maxDifficulty = 1.0,
                maxSignificance = 1.0,
                random = Random(100L),
            )

        newUniverseScienceData.basicResearchProjectDataMap.forEach {
            assert(it.value.basicResearchField == BasicResearchField.MATHEMATICS)
        }

        assert(
            newUniverseScienceData.basicResearchProjectDataMap.keys.toList()
                .sorted() == (0..9).toList()
        )

        newUniverseScienceData.appliedResearchProjectDataMap.forEach {
            assert(it.value.appliedResearchField == AppliedResearchField.ENERGY_TECHNOLOGY)
        }

        assert(
            newUniverseScienceData.appliedResearchProjectDataMap.keys.toList()
                .sorted() == (0..9).toList()
        )
    }
}