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
    override val description: String = "Increase mathematics knowledge level by $importance"

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
    override val description: String = "Increase physics knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.physicsResearchLevel += importance
    }
}

@Serializable
data class MaterialKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase material knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.materialResearchLevel += importance
    }
}

@Serializable
data class ComputerKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase computer knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.computerResearchLevel += importance
    }
}

@Serializable
data class MechanicsKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase mechanics knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.mechanicsResearchLevel += importance
    }
}

@Serializable
data class MedicineKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase medicine knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.medicineResearchLevel += importance
    }
}

@Serializable
data class EconomyKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase economy knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.economyResearchLevel += importance
    }
}

@Serializable
data class PoliticsKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase politics knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.politicsResearchLevel += importance
    }
}

@Serializable
data class SociologyKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase sociology knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.sociologyResearchLevel += importance
    }
}

@Serializable
data class PsychologyKnowledge(
    override val knowledgeId: Int,
    val importance: Double,
    override val xCor: Double,
    override val yCor: Double,
    override val difficulty: Double,
    override val referenceKnowledgeIdList: List<Int>,
    override val referenceTechnologyIdList: List<Int>,
) : SingleKnowledgeData() {
    override val description: String = "Increase psychology knowledge level by $importance"

    override fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData) {
        mutableKnowledgeData.psychologyResearchLevel += importance
    }
}