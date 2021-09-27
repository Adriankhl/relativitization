package relativitization.universe.data.component.science.product

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import relativitization.universe.data.component.popsystem.pop.labourer.factory.FactoryInternalData
import relativitization.universe.data.component.popsystem.pop.labourer.factory.MutableFactoryInternalData

@Serializable
data class ScienceProductData(
    val maxShipRestMass: Double = 10000.0,
    val maxShipEnginePowerByRestMass: Double = 1E-6,
    val idealFactoryMap: Map<ResourceType, FactoryInternalData> = mapOf(),
    val fuelLogisticsLossFractionPerDistance: Double = 0.9,
    val resourceLogisticsLossFractionPerDistance: Double = 0.9,
)

@Serializable
data class MutableScienceProductData(
    var maxShipRestMass: Double = 10000.0,
    var maxShipEnginePowerByRestMass: Double = 1E-6,
    var idealFactoryMap: MutableMap<ResourceType, MutableFactoryInternalData> = mutableMapOf(),
    var fuelLogisticsLossFractionPerDistance: Double = 0.9,
    var resourceLogisticsLossFractionPerDistance: Double = 0.9,
)