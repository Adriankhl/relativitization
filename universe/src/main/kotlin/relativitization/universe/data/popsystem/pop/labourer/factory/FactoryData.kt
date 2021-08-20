package relativitization.universe.data.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.MutableResourceQualityData
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType

/**
 * Data for a factory of labour pop
 *
 * @property outputResource the output resource type
 * @property maxOutputResourceQualityData maximum output resource quality
 * @property maxOutputAmount maximum output resource amount
 * @property lastOutputAmount the output amount in the latest turn
 * @property inputResourceMap map the input resource type to the input-related data
 * @property fuelRestMassConsumptionRate fuel consumption rate
 */
@Serializable
data class FactoryData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double = inputResourceMap[resourceType]?.amountPerOutputUnit ?: 0.0
        return amountPerUnit * maxOutputAmount
    }
}

@Serializable
data class MutableFactoryData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    var maxOutputAmount: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var inputResourceMap: MutableMap<ResourceType, InputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 0.0,
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