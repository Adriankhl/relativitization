package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.UniverseData
import relativitization.universe.data.serializer.DataSerializer

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
    fun newUniverseScienceData(universeData: UniverseData): UniverseScienceData {
        val mutableUniverseScienceData: MutableUniverseScienceData = DataSerializer.copy(
            universeData.universeScienceData
        )

        return DataSerializer.copy(mutableUniverseScienceData)
    }
}