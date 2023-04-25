package relativitization.universe.game.generate.random.science


import relativitization.universe.game.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.game.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.game.data.global.components.UniverseScienceData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.AppliedResearchProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.BasicResearchProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.ProjectGenerationData
import relativitization.universe.game.data.global.components.defaults.science.knowledge.UniverseProjectGenerationData
import kotlin.random.Random
import kotlin.test.Test

internal class DefaultGenerateUniverseScienceDataTest {
    @Test
    fun testGenerateDefaultField() {
        val universeScienceData = UniverseScienceData(
            universeProjectGenerationData = UniverseProjectGenerationData(
                basicResearchProjectGenerationDataList = listOf(
                    BasicResearchProjectGenerationData(
                        basicResearchField = BasicResearchField.MATHEMATICS,
                        projectGenerationData = ProjectGenerationData(
                            centerX = 0.0,
                            centerY = 0.0,
                            range = 1.0,
                            weight = 1.0,
                        )
                    )
                ),
                appliedResearchProjectGenerationDataList = listOf(
                   AppliedResearchProjectGenerationData(
                       appliedResearchField = AppliedResearchField.ENERGY_TECHNOLOGY,
                       projectGenerationData = ProjectGenerationData(
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
                universeScienceData = universeScienceData,
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