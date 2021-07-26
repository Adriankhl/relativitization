package relativitization.universe.data.science.technology

import kotlinx.serialization.Serializable

@Serializable
abstract class SingleTechnologyData {
    abstract val technologyId: Int

    // x and y coordinate in the knowledge space
    abstract val xCor: Double
    abstract val yCor: Double

    abstract val difficulty: Double

    abstract val referenceKnowledgeIdList: List<Int>
    abstract val referenceTechnologyIdList: List<Int>

    abstract val description: String

    abstract fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData)
}

/**
 * Represent the effect of a combination of SingleTechnologyData
 *
 * @property startFromTechnologyId technologies with id lower than this value are all included
 * @property technologyIdList included technologies with id higher than the minTechnologyId
 * @property maxShipRestMass maximum rest mass of a ship to manufacture
 * @property shipEngineLevel related to the maximum delta fuel mass of a ship, should depend on the
 * total rest mass of the ship
 */
@Serializable
data class TechnologyData(
    val startFromTechnologyId: Int = 0,
    val technologyIdList: List<Int> = listOf(),
    val maxShipRestMass: Double = 10000.0,
    val shipEngineLevel: Double = 1.0,
)

@Serializable
data class MutableTechnologyData(
    var startFromTechnologyId: Int = 0,
    val technologyIdList: MutableList<Int> = mutableListOf(),
    var maxShipRestMass: Double = 10000.0,
    var shipEngineLevel: Double = 1.0,
)

/**
 * For generating a single technology data in a field
 *
 * @property centerX the x coordinate of the center of the field in the knowledge plane
 * @property centerY the y coordinate of the center of the field in the knowledge plane
 * @property range the dispersion of this field
 * @property weight the likelihood that a new technology is in this field
 */
@Serializable
data class TechnologyFieldGenerationData(
    val centerX: Double = 0.0,
    val centerY: Double = 0.0,
    val range: Double = 1.0,
    val weight: Double = 1.0,
)

@Serializable
data class MutableTechnologyFieldGenerationData(
    var centerX: Double = 0.0,
    var centerY: Double = 0.0,
    var range: Double = 1.0,
    var weight: Double = 1.0,
)

@Serializable
data class TechnologyGenerationData(
    val maxShipRestMassTechnologyGenerationData: TechnologyFieldGenerationData = TechnologyFieldGenerationData(),
    val shipEngineTechnologyGenerationData: TechnologyFieldGenerationData = TechnologyFieldGenerationData(),
)

@Serializable
data class MutableTechnologyGenerationData(
    var maxShipRestMassTechnologyGenerationData: MutableTechnologyFieldGenerationData = MutableTechnologyFieldGenerationData(),
    var shipEngineTechnologyGenerationData: MutableTechnologyFieldGenerationData = MutableTechnologyFieldGenerationData(),
)