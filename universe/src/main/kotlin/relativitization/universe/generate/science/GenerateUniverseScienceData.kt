package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

object DefaultGenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun generate(universeData: UniverseData): UniverseScienceData {
        val numGeneration: Int = 100

        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )

        for (i in 1..numGeneration) {
            val newKnowledgeData: SingleKnowledgeData = generateSingleKnowledgeData(
                mutableUniverseScienceData
            )
        }

        return DataSerializer.copy(mutableUniverseScienceData)
    }

    /**
     * Generate Single knowledge data
     *
     * @param mutableUniverseScienceData universe science data
     */
    private fun generateSingleKnowledgeData(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): SingleKnowledgeData {
        TODO()
    }

    /**
     * Generate single technology data
     *
     * @param mutableUniverseScienceData universe science data
     */
    private fun generateSingleTechnologyData(
        mutableUniverseScienceData: MutableUniverseScienceData,
    ): SingleTechnologyData {
        TODO()
    }
}