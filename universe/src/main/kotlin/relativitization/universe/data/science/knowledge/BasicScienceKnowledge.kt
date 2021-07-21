package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
data class MathematicsKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.mathematicsResearchLevel += importance
    }
}

@Serializable
data class PhysicsKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.physicsResearchLevel += importance
    }
}