package relativitization.universe.data.component.science.product

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.labourer.factory.InputResourceData

@Serializable
data class IdealFactoryData(
    val outputResource: ResourceType = ResourceType.FUEL,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val size: Double = 0.0,
)

@Serializable
data class MutableIdealFactoryData(
    var outputResource: ResourceType = ResourceType.FUEL,
    var maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    var maxOutputAmount: Double = 0.0,
    val inputResourceMap: MutableMap<ResourceType, InputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var size: Double = 0.0,
)