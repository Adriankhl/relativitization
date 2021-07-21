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

    abstract fun updateTechnologyData(mutableTechnologyData: MutableTechnologyData)
}

/**
 * Represent the effect of a combination of SingleTechnologyData
 *
 * @property minTechnologyId technologies with id lower than this value are all included
 * @property technologyIdList included technologies with id higher than the minTechnologyId
 * @property maxShipRestMass maximum rest mass of a ship to manufacture
 * @property shipEngineTechnology related to the maximum delta fuel mass of a ship
 */
@Serializable
data class TechnologyData(
    val minTechnologyId: Int = 0,
    val technologyIdList: List<Int> = listOf(),
    val maxShipRestMass: Double = 10000.0,
    val shipEngineTechnology: Double = 1.0,
)

@Serializable
data class MutableTechnologyData(
    var minTechnologyId: Int = 0,
    val technologyIdList: MutableList<Int> = mutableListOf(),
    var maxShipRestMass: Double = 10000.0,
    var shipEngineTechnology: Double = 1.0,
)