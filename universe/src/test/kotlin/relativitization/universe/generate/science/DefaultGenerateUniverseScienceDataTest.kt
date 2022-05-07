package relativitization.universe.generate.science


import relativitization.universe.data.components.defaults.science.knowledge.AppliedResearchField
import relativitization.universe.data.components.defaults.science.knowledge.BasicResearchField
import relativitization.universe.data.global.components.UniverseScienceData
import relativitization.universe.generate.random.science.DefaultGenerateUniverseScienceData
import kotlin.test.Test

internal class DefaultGenerateUniverseScienceDataTest {
    @Test
    fun testGenerateDefaultField() {
        val universeScienceData: UniverseScienceData = UniverseScienceData()

        val newUniverseScienceData: UniverseScienceData =
            DefaultGenerateUniverseScienceData.generate(
                universeScienceData = universeScienceData,
                numBasicResearchProjectGenerate = 10,
                numAppliedResearchProjectGenerate = 10,
                maxBasicReference = 10,
                maxAppliedReference = 10,
                maxDifficulty = 1.0,
                maxSignificance = 1.0
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