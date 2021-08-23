package relativitization.universe.data.subsystem.popsystem

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.pop.AllPopData
import relativitization.universe.data.subsystem.popsystem.pop.MutableAllPopData

@Serializable
data class Carrier(
    val coreRestMass: Double = 1.0,
    val fuelRestMass: Double = 0.0,
    val maxDeltaFuelRestMass: Double = 0.0,
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val allPopData: AllPopData = AllPopData(),
)

@Serializable
data class MutableCarrier(
    var coreRestMass: Double = 1.0,
    var fuelRestMass: Double = 0.0,
    var maxDeltaFuelRestMass: Double = 0.0,
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var allPopData: MutableAllPopData = MutableAllPopData(),
)

enum class CarrierType {
    STELLAR,
    PLANETARY,
    SPACESHIP
}