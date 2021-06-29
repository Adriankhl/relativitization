package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.SingleTechnologyData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseScienceData(
    val allSingleKnowledgeDataList: List<SingleKnowledgeData> = listOf(),
    val allSingleTechnologyDataList: List<SingleTechnologyData> = listOf(),
)

@Serializable
data class MutableUniverseScienceData(
    val allSingleKnowledgeDataList: MutableList<SingleKnowledgeData> = mutableListOf(),
    val allSingleTechnologyDataList: MutableList<SingleTechnologyData> = mutableListOf(),
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
        universeData.universeData4D
        mutableUniverseScienceData.allSingleKnowledgeDataList
    }
}