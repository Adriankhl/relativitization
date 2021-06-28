package relativitization.universe.data.science

import kotlinx.serialization.Serializable

@Serializable
data class PlayerScienceData(
    val knowledgeDataList: List<KnowledgeData> = listOf(),
    val technologyDataList: List<TechnologyData> = listOf(),
)

@Serializable
data class MutablePlayerScienceData(
    val knowledgeDataList: MutableList<KnowledgeData> = mutableListOf(),
    val technologyDataList: MutableList<TechnologyData> = mutableListOf(),
)