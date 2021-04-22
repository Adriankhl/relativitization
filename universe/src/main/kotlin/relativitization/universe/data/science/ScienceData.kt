package relativitization.universe.data.science

import kotlinx.serialization.Serializable

@Serializable
data class ScienceData(
    val knowledgeList: List<Knowledge> = listOf()
)

@Serializable
data class MutableScienceData(
    val knowledgeList: MutableList<Knowledge> = mutableListOf()
)