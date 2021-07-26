package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

@Serializable
sealed class SingleKnowledgeData {
    abstract val knowledgeId: Int

    abstract val knowledgeField: KnowledgeField

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
 * @property weight the likelihood that a new technology is in this field
 */
@Serializable
data class KnowledgeFieldGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
    val weight: Double = 1.0,
)

@Serializable
data class MutableKnowledgeFieldGenerationData(
    var centerX: Double = 0.0,
    var centerY: Double = 0.0,
    var range: Double = 1.0,
    var weight: Double = 1.0,
)

@Serializable
data class KnowledgeGenerationData(
    val knowledgeGenerationDataMap: Map<KnowledgeField, KnowledgeGenerationData> = mapOf(
        KnowledgeField.MATHEMATICS to KnowledgeGenerationData(),
        KnowledgeField.PHYSICS to KnowledgeGenerationData(),
        KnowledgeField.MATERIAL to KnowledgeGenerationData(),
        KnowledgeField.COMPUTER to KnowledgeGenerationData(),
        KnowledgeField.MECHANICS to KnowledgeGenerationData(),
        KnowledgeField.MEDICINE to KnowledgeGenerationData(),
        KnowledgeField.ECONOMY to KnowledgeGenerationData(),
        KnowledgeField.POLITICS to KnowledgeGenerationData(),
        KnowledgeField.SOCIOLOGY to KnowledgeGenerationData(),
        KnowledgeField.PSYCHOLOGY to KnowledgeGenerationData(),
    )
)

@Serializable
data class MutableKnowledgeGenerationData(
    val knowledgeGenerationDataMap: Map<KnowledgeField, MutableKnowledgeGenerationData> = mapOf(
        KnowledgeField.MATHEMATICS to MutableKnowledgeGenerationData(),
        KnowledgeField.PHYSICS to MutableKnowledgeGenerationData(),
        KnowledgeField.MATERIAL to MutableKnowledgeGenerationData(),
        KnowledgeField.COMPUTER to MutableKnowledgeGenerationData(),
        KnowledgeField.MECHANICS to MutableKnowledgeGenerationData(),
        KnowledgeField.MEDICINE to MutableKnowledgeGenerationData(),
        KnowledgeField.ECONOMY to MutableKnowledgeGenerationData(),
        KnowledgeField.POLITICS to MutableKnowledgeGenerationData(),
        KnowledgeField.SOCIOLOGY to MutableKnowledgeGenerationData(),
        KnowledgeField.PSYCHOLOGY to MutableKnowledgeGenerationData(),
    )
)

enum class KnowledgeField(val value: String) {
    MATHEMATICS("Mathematics"),
    PHYSICS("Physics"),
    MATERIAL("Material"),
    COMPUTER("Computer"),
    MECHANICS("Mechanics"),
    MEDICINE("Medicine"),
    ECONOMY("Economy"),
    POLITICS("Politics"),
    SOCIOLOGY("Sociology"),
    PSYCHOLOGY("Psychology"),
    ;

    override fun toString(): String {
        return value
    }
}