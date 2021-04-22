package relativitization.universe.data.popsystems

import kotlinx.serialization.Serializable

@Serializable
data class Carrier(
    val restMass: Double = 0.0,
    val carrierType: CarrierType = CarrierType.SPACESHIP,
)

@Serializable
data class MutableCarrier(
    var restMass: Double = 0.0,
    var carrierType: CarrierType = CarrierType.SPACESHIP,
)

enum class CarrierType {
    STELLAR,
    PLANETARY,
    SPACESHIP
}