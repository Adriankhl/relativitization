package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
abstract class KnowledgeData {
    abstract val knowledgeId: Int
}