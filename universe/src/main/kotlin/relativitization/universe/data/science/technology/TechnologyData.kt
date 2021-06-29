package relativitization.universe.data.science.technology

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleTechnologyData {
    abstract val technologyId: Int
}

@Serializable
data class TechnologyData(
    val maxKnowledgeId: Int = 0,
)

@Serializable
data class MutableTechnologyData(
    var maxKnowledgeId: Int = 0,
)