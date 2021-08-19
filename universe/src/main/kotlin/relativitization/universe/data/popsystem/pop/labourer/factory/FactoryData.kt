package relativitization.universe.data.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.economy.ResourceQualityData
import relativitization.universe.data.economy.ResourceType

@Serializable
data class FactoryData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
)

@Serializable
data class MutableFactoryData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    var inputResourceMap: MutableMap<ResourceType, InputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 0.0,
)

@Serializable
data class InputResourceData(
    val amount: Double = 1.0,
)

@Serializable
data class MutableInputResourceData(
    var amount: Double = 1.0,
)