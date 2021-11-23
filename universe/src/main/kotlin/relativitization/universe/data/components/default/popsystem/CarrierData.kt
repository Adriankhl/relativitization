package relativitization.universe.data.components.default.popsystem

import kotlinx.serialization.Serializable

@Serializable
data class CarrierData(
    val coreRestMass: Double = 1.0,
    val maxMovementDeltaFuelRestMass: Double = 0.0,
    val size: Double = 100.0,
    val idealPopulation: Double = 100.0,
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val allPopData: relativitization.universe.data.components.default.popsystem.pop.AllPopData = relativitization.universe.data.components.default.popsystem.pop.AllPopData(),
)

@Serializable
data class MutableCarrierData(
    var coreRestMass: Double = 1.0,
    var maxMovementDeltaFuelRestMass: Double = 0.0,
    var size: Double = 100.0,
    var idealPopulation: Double = 100.0,
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var allPopData: relativitization.universe.data.components.default.popsystem.pop.MutableAllPopData = relativitization.universe.data.components.default.popsystem.pop.MutableAllPopData(),
)

enum class CarrierType {
    STELLAR,
    PLANETARY,
    SPACESHIP
}