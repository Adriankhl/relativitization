package relativitization.universe.data.science.knowledge

import kotlinx.serialization.Serializable

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
    val generationDataMap: Map<KnowledgeField, KnowledgeFieldGenerationData> = mapOf(
        KnowledgeField.MATHEMATICS to KnowledgeFieldGenerationData(),
        KnowledgeField.PHYSICS to KnowledgeFieldGenerationData(),
        KnowledgeField.MATERIAL to KnowledgeFieldGenerationData(),
        KnowledgeField.COMPUTER to KnowledgeFieldGenerationData(),
        KnowledgeField.MECHANICS to KnowledgeFieldGenerationData(),
        KnowledgeField.MEDICINE to KnowledgeFieldGenerationData(),
        KnowledgeField.ECONOMY to KnowledgeFieldGenerationData(),
        KnowledgeField.POLITICS to KnowledgeFieldGenerationData(),
        KnowledgeField.SOCIOLOGY to KnowledgeFieldGenerationData(),
        KnowledgeField.PSYCHOLOGY to KnowledgeFieldGenerationData(),
    )
)

@Serializable
data class MutableKnowledgeGenerationData(
    val generationDataMap: Map<KnowledgeField, MutableKnowledgeFieldGenerationData> = mapOf(
        KnowledgeField.MATHEMATICS to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.PHYSICS to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.MATERIAL to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.COMPUTER to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.MECHANICS to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.MEDICINE to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.ECONOMY to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.POLITICS to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.SOCIOLOGY to MutableKnowledgeFieldGenerationData(),
        KnowledgeField.PSYCHOLOGY to MutableKnowledgeFieldGenerationData(),
    )
)