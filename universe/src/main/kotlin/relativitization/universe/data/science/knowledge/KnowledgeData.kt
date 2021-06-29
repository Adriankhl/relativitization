package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleKnowledgeData {
    abstract val knowledgeId: Int

    abstract fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData)
}

@Serializable
data class KnowledgeData(
    val minKnowledgeId: Int = 0,
    val knowledgeIdList: List<Int> = listOf(),
)

@Serializable
data class MutableKnowledgeData(
    var minKnowledgeId: Int = 0,
    val knowledgeIdList: MutableList<Int> = mutableListOf(),
)