package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleKnowledgeData {
    abstract val knowledgeId: Int

    // x and y coordinate in the knowledge space
    abstract val xCor: Double
    abstract val yCor: Double

    abstract val difficulty: Double

    abstract val referenceKnowledgeIdList: List<Int>
    abstract val referenceTechnologyIdList: List<Int>

    abstract val description: String

    abstract fun updateKnowledgeData(mutableKnowledgeData: MutableKnowledgeData)
}

/**
 * Represent the effect of a combination of SingleKnowledgeData
 *
 * @property startFromKnowledgeId knowledge with id lower than this value are all included
 * @property knowledgeIdList include knowledge with id higher than the minTechnologyId
 */
@Serializable
data class KnowledgeData(
    val startFromKnowledgeId: Int = 0,
    val knowledgeIdList: List<Int> = listOf(),
    val mathematicsResearchLevel: Double = 0.0,
    val physicsResearchLevel: Double = 0.0,
    val materialResearchLevel: Double = 0.0,
    val computerResearchLevel: Double = 0.0,
    val mechanicsResearchLevel: Double = 0.0,
    val medicineResearchLevel: Double = 0.0,
    val economyResearchLevel: Double = 0.0,
    val politicsResearchLevel: Double = 0.0,
    val sociologyResearchLevel: Double = 0.0,
    val psychologyResearchLevel: Double = 0.0,
)

@Serializable
data class MutableKnowledgeData(
    var startFromKnowledgeId: Int = 0,
    val knowledgeIdList: MutableList<Int> = mutableListOf(),
    var mathematicsResearchLevel: Double = 0.0,
    var physicsResearchLevel: Double = 0.0,
    var materialResearchLevel: Double = 0.0,
    var computerResearchLevel: Double = 0.0,
    var mechanicsResearchLevel: Double = 0.0,
    var medicineResearchLevel: Double = 0.0,
    var economyResearchLevel: Double = 0.0,
    var politicsResearchLevel: Double = 0.0,
    var sociologyResearchLevel: Double = 0.0,
    var psychologyResearchLevel: Double = 0.0,
)

/**
 * For generating a single knowledge data in a field
 *
 * @property centerX the x coordinate of the center of the field in the knowledge plane
 * @property centerY the y coordinate of the center of the field in the knowledge plane
 * @property range the dispersion of this field
 */
@Serializable
data class KnowledgeFieldGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
)

@Serializable
data class MutableKnowledgeFieldGenerationData(
    var centerX: Double = 0.0,
    var centerY: Double = 0.0,
    var range: Double = 1.0,
)

@Serializable
data class KnowledgeGenerationData(
    val mathematicsKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val physicsKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val materialKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val computerKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val mechanicsKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val medicineKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val economyKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val politicsKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val sociologyKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val psychologyKnowledgeGenerationData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
)

@Serializable
data class MutableKnowledgeGenerationData(
    var mathematicsKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var physicsKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var materialKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var computerKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var mechanicsKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var medicineKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var economyKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var politicsKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var sociologyKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var psychologyKnowledgeGenerationData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
)