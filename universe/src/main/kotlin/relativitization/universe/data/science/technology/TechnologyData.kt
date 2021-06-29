package relativitization.universe.data.science.technology

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleTechnologyData {
    abstract val technologyId: Int

    abstract val referenceKnowledgeIdList: List<Int>
    abstract val referenceTechnologyIdList: List<Int>

    abstract fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData)
}

@Serializable
data class TechnologyData(
    val minKnowledgeId: Int = 0,
    val technologyIdList: List<Int> = listOf(),
)

@Serializable
data class MutableTechnologyData(
    var minKnowledgeId: Int = 0,
    val technologyIdList: MutableList<Int> = mutableListOf(),
)