package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

object GenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun generate(universeData: UniverseData): UniverseScienceData {
        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )
        return DataSerializer.copy(mutableUniverseScienceData)
    }

    /**
     * Generate knowledge list
     *
     * @param universeScienceData universe science data
     * @param numNewSingleKnowledgeData number of new single knowledge data
     */
    private fun generateKnowledgeList(
        universeScienceData: UniverseScienceData,
        numNewSingleKnowledgeData: Int,
    ): List<SingleKnowledgeData> {
        return listOf()
    }

    /**
     * Generate technology list
     *
     * @param universeScienceData universe science data
     * @param numNewSingleTechnologyData number of new single knowledge data
     */
    private fun generateTechnologyList(
        universeScienceData: UniverseScienceData,
        numNewSingleTechnologyData: Int,
    ): List<SingleTechnologyData> {
        return listOf()
    }
}