package relativitization.universe.generate.science

import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.science.MutableUniverseScienceData
import relativitization.universe.data.science.UniverseScienceData
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.min

object DefaultGenerateUniverseScienceData {
    private val logger = RelativitizationLogManager.getLogger()

    fun generate(
        universeData: UniverseData,
        numKnowledgeGenerate: Int,
        numTechnologyGenerate: Int,
    ): UniverseScienceData {
        val universeScienceData: UniverseScienceData = universeData.universeScienceData
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeScienceData
        )

        val minGenerate: Int = min(numKnowledgeGenerate, numTechnologyGenerate)

        for (i in 1..minGenerate) {
            val newKnowledgeData: SingleKnowledgeData = generateSingleKnowledgeData(
                mutableUniverseScienceData
            )
            mutableUniverseScienceData.addSingleKnowledgeData(newKnowledgeData)
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