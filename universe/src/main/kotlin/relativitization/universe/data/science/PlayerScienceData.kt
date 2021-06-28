package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.science.knowledge.KnowledgeData
import relativitization.universe.data.science.technology.TechnologyData

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