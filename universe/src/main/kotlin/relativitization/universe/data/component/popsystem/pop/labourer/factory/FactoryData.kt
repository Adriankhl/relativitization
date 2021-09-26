package relativitization.universe.data.component.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType

/**
 * Data for a factory of labour pop
 *
 * @property ownerPlayerId the owner of this factory
 * @property isOpened whether this factory is opened
 * @property outputResource the output resource type
 * @property maxOutputResourceQualityData maximum output resource quality
 * @property maxOutputAmount maximum output resource amount
 * @property lastOutputAmount the output amount in the latest turn
 * @property inputResourceMap map the input resource type to the input-related data
 * @property fuelRestMassConsumptionRate fuel consumption rate
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property maxNumEmployee max number of employee
 * @property lastNumEmployee number of employee in the last turn
 * @property size the size of this factory
 */
@Serializable
data class FactoryData(
    val ownerPlayerId: Int = -1,
    val isOpened: Boolean = true,
    val outputResource: ResourceType = ResourceType.FUEL,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
    val storedFuelRestMass: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
    val size: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double = inputResourceMap[resourceType]?.amountPerOutputUnit ?: 0.0
        return amountPerUnit * maxOutputAmount
    }
}

@Serializable
data class MutableFactoryData(
    var ownerPlayerId: Int = -1,
    var isOpened: Boolean = true,
    var outputResource: ResourceType = ResourceType.FUEL,
    var maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var maxOutputAmount: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 0.0,
    var storedFuelRestMass: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
    var size: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double = inputResourceMap[resourceType]?.amountPerOutputUnit ?: 0.0
        return amountPerUnit * maxOutputAmount
    }
}

/**
 * Input resource related data
 *
 * @property maxInputResourceQualityData maximum input resource quality, quality exceeding this
 * won't improve the output quality
 * @property amountPerOutputUnit amount of resource required to produce one unit of output resource
 * @property lastInputAmount the input amount in the latest turn
 */
@Serializable
data class InputResourceData(
    val maxInputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val amountPerOutputUnit: Double = 1.0,
    val lastInputAmount: Double = 0.0,
)

@Serializable
data class MutableInputResourceData(
    var maxInputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var amountPerOutputUnit: Double = 1.0,
    var lastInputAmount: Double = 0.0,
)