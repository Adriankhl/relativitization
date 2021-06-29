package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleKnowledgeData {
    abstract val knowledgeId: Int
}

@Serializable
data class KnowledgeData(
    val maxKnowledgeId: Int = 0,
)

@Serializable
data class MutableKnowledgeData(
    var maxKnowledgeId: Int = 0,
)