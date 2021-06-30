package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
data class PhysicsKnowledge(
    override val knowledgeId: Int,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
    val importance: Double,
) : SingleKnowledgeData() {
    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.physicsResearchLevel += importance
    }
}