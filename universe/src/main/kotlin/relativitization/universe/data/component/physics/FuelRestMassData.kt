package relativitization.universe.data.component.physics

import kotlinx.serialization.Serializable

@Serializable
data class FuelRestMassData(
    val movement: Double = 0.0,
    val production: Double = 0.0,
    val trade: Double = 0.0,
) {
    fun total(): Double = movement + production + trade
}

@Serializable
data class MutableFuelRestMassData(
    var movement: Double = 0.0,
    var production: Double = 0.0,
    var trade: Double = 0.0,
) {
    fun total(): Double = movement + production + trade
}