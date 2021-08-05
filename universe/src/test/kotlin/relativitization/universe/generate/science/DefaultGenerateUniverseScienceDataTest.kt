package relativitization.universe.generate.science


import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.AppliedResearchField
import relativitization.universe.data.science.knowledge.BasicResearchField
import kotlin.test.Test

internal class DefaultGenerateUniverseScienceDataTest {
    @Test
    fun testGenerateDefaultField() {
        val universeScienceData: UniverseScienceData = UniverseScienceData()

        val newUniverseScienceData: UniverseScienceData = DefaultGenerateUniverseScienceData.generate(
            universeScienceData,
            10,
            10
        )

        newUniverseScienceData.basicResearchProjectDataMap.forEach {
            assert(it.value.basicResearchField == BasicResearchField.MATHEMATICS)
        }

        assert(newUniverseScienceData.basicResearchProjectDataMap.keys.toList().sorted() == (0..9).toList())

        newUniverseScienceData.appliedResearchProjectDataMap.forEach {
            assert(it.value.appliedResearchField == AppliedResearchField.ENERGY_TECHNOLOGY)
        }

        assert(newUniverseScienceData.appliedResearchProjectDataMap.keys.toList().sorted() == (0..9).toList())
    }
}