package relativitization.universe.data.science

import kotlinx.serialization.Serializable
import relativitization.universe.data.science.knowledge.SingleKnowledgeData
import relativitization.universe.data.science.technology.SingleTechnologyData

@Serializable
data class PlayerScienceData(
    val singleKnowledgeDataList: List<SingleKnowledgeData> = listOf(),
    val singleTechnologyDataList: List<SingleTechnologyData> = listOf(),
)

@Serializable
data class MutablePlayerScienceData(
    val singleKnowledgeDataList: MutableList<SingleKnowledgeData> = mutableListOf(),
    val singleTechnologyDataList: MutableList<SingleTechnologyData> = mutableListOf(),
)