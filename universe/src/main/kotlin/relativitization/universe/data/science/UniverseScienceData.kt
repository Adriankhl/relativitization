package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.data.science.technology.*
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseScienceData(
    val allSingleKnowledgeDataList: List<SingleKnowledgeData> = listOf(),
    val allSingleTechnologyDataList: List<SingleTechnologyData> = listOf(),
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val commonSenseTechnologyData: TechnologyData = TechnologyData(),
    val knowledgeGenerationData: KnowledgeGenerationData = KnowledgeGenerationData(),
    val technologyGenerationData: TechnologyGenerationData = TechnologyGenerationData(),
)

@Serializable
data class MutableUniverseScienceData(
    val allSingleKnowledgeDataList: MutableList<SingleKnowledgeData> = mutableListOf(),
    val allSingleTechnologyDataList: MutableList<SingleTechnologyData> = mutableListOf(),
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var commonSenseTechnologyData: MutableTechnologyData = MutableTechnologyData(),
    var knowledgeGenerationData: MutableKnowledgeGenerationData = MutableKnowledgeGenerationData(),
    var technologyGenerationData: MutableTechnologyGenerationData = MutableTechnologyGenerationData(),
)

object ProcessUniverseScienceData {

    private val logger = RelativitizationLogManager.getLogger()

    // list of all possible name of
    val universeScienceDataProcessNameList: List<String> = listOf(
        "DefaultUniverseScienceDataProcess",
        "EmptyUniverseScienceDataProcess"
    )

    fun newUniverseScienceData(universeData: UniverseData): UniverseScienceData {
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeData.universeScienceData
        )

        when (universeData.universeSettings.universeScienceDataProcessName) {
            "DefaultUniverseScienceDataProcess" -> defaultUniverseScienceDataProcess(
                universeData,
                mutableUniverseScienceData
            )
            "EmptyUniverseScienceDataProcess" -> {}
            else -> {
                logger.error("Invalid universeScienceDataProcessName, use default process")
                defaultUniverseScienceDataProcess(
                    universeData,
                    mutableUniverseScienceData
                )
            }
        }

        return DataSerializer.copy(mutableUniverseScienceData)
    }

    private fun defaultUniverseScienceDataProcess(
        universeData: UniverseData,
        mutableUniverseScienceData: MutableUniverseScienceData
    ) {
    }
}