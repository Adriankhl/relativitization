package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager

@Serializable
data class UniverseScienceData(
    val allKnowledgeDataList: List<KnowledgeData> = listOf(),
    val allTechnologyDataList: List<TechnologyData> = listOf(),
)

@Serializable
data class MutableUniverseScienceData(
    val allKnowledgeDataList: MutableList<KnowledgeData> = mutableListOf(),
    val allTechnologyDataList: MutableList<TechnologyData> = mutableListOf(),
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

    fun defaultUniverseScienceDataProcess(
        universeData: UniverseData,
        mutableUniverseScienceData: MutableUniverseScienceData
    ) {

    }
}