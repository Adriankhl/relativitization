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
 * @property minKnowledgeId knowledge with id lower than this value are all included
 * @property knowledgeIdList include knowledge with id higher than the minTechnologyId
 */
@Serializable
data class KnowledgeData(
    val minKnowledgeId: Int = 0,
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
    var minKnowledgeId: Int = 0,
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
    val mathematicsKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val physicsKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val materialKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val computerKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val mechanicsKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val medicineKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val economyKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val politicsKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val sociologyKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
    val psychologyKnowledgeGenerateData: KnowledgeFieldGenerationData = KnowledgeFieldGenerationData(),
)

@Serializable
data class MutableKnowledgeGenerationData(
    var mathematicsKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var physicsKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var materialKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var computerKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var mechanicsKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var medicineKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var economyKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var politicsKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var sociologyKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
    var psychologyKnowledgeGenerateData: MutableKnowledgeFieldGenerationData = MutableKnowledgeFieldGenerationData(),
)