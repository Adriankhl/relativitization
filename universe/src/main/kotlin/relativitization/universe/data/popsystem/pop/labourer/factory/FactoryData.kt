package relativitization.universe.data.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType

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

@Serializable
data class InputResourceData(
    val amountPerOutputUnit: Double = 1.0,
    val lastInputAmount: Double = 0.0,
)

@Serializable
data class MutableInputResourceData(
    var amountPerOutputUnit: Double = 1.0,
    var lastInputAmount: Double = 0.0,
)