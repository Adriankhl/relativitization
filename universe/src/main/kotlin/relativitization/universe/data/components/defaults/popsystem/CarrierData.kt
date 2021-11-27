package relativitization.universe.data.components.defaults.popsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.pop.AllPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableAllPopData

enum class CarrierType {
    STELLAR,
    SPACESHIP
}

@Serializable
data class CarrierData(
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val carrierInternalData: CarrierInternalData = CarrierInternalData(),
    val allPopData: AllPopData = AllPopData(),
)

@Serializable
data class MutableCarrierData(
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var carrierInternalData: MutableCarrierInternalData = MutableCarrierInternalData(),
    var allPopData: MutableAllPopData = MutableAllPopData(),
)

@Serializable
data class CarrierInternalData(
    val coreRestMass: Double = 1.0,
    val maxMovementDeltaFuelRestMass: Double = 0.0,
    val size: Double = 100.0,
    val idealPopulation: Double = 100.0,
)

@Serializable
data class MutableCarrierInternalData(
    var coreRestMass: Double = 1.0,
    var maxMovementDeltaFuelRestMass: Double = 0.0,
    var size: Double = 100.0,
    var idealPopulation: Double = 100.0,
)