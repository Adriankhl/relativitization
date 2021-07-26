package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.knowledge.*
import relativitization.universe.data.science.technology.*
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseScienceData(
    val commonSenseKnowledgeData: KnowledgeData = KnowledgeData(),
    val commonSenseTechnologyData: TechnologyData = TechnologyData(),
    val allSingleKnowledgeDataMap: Map<Int, SingleKnowledgeData> = mapOf(),
    val allSingleTechnologyDataMap: Map<Int, SingleTechnologyData> = mapOf(),
    val knowledgeGenerationData: KnowledgeGenerationData = KnowledgeGenerationData(),
    val technologyGenerationData: TechnologyGenerationData = TechnologyGenerationData(),
)

@Serializable
data class MutableUniverseScienceData(
    var commonSenseKnowledgeData: MutableKnowledgeData = MutableKnowledgeData(),
    var commonSenseTechnologyData: MutableTechnologyData = MutableTechnologyData(),
    val allSingleKnowledgeDataMap: MutableMap<Int, SingleKnowledgeData> = mutableMapOf(),
    val allSingleTechnologyDataMap: MutableMap<Int, SingleTechnologyData> = mutableMapOf(),
    var knowledgeGenerationData: MutableKnowledgeGenerationData = MutableKnowledgeGenerationData(),
    var technologyGenerationData: MutableTechnologyGenerationData = MutableTechnologyGenerationData(),
) {
    fun addSingleKnowledgeData(singleKnowledgeData: SingleKnowledgeData) {
        when {
            allSingleKnowledgeDataMap.containsKey(singleKnowledgeData.knowledgeId) -> {
                logger.error("new single knowledge data has duplicate id, ignore the new data")
            }
            allSingleKnowledgeDataMap.keys.maxOrNull() ?: -1 >= singleKnowledgeData.knowledgeId -> {
                logger.error("new single knowledge data has id smaller than the maximum id")

                // Still add the knowledge data as long as there is no duplication
                allSingleKnowledgeDataMap[singleKnowledgeData.knowledgeId] = singleKnowledgeData
            }
            else -> {
                allSingleKnowledgeDataMap[singleKnowledgeData.knowledgeId] = singleKnowledgeData
            }
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

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