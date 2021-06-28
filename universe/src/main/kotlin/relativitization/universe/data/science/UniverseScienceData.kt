package relativitization.universe.data.science

import kotlinx.serialization.Serializable

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
