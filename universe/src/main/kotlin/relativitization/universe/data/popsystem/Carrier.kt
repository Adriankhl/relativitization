package relativitization.universe.data.popsystem

import kotlinx.serialization.Serializable

@Serializable
data class Carrier(
    val restMass: Double = 1.0,
    val carrierType: CarrierType = CarrierType.SPACESHIP,
    val allPopData: AllPopData = AllPopData(),
)

@Serializable
data class MutableCarrier(
    var restMass: Double = 1.0,
    var carrierType: CarrierType = CarrierType.SPACESHIP,
    var allPopData: MutableAllPopData = MutableAllPopData(),
)

enum class CarrierType {
    STELLAR,
    PLANETARY,
    SPACESHIP
}