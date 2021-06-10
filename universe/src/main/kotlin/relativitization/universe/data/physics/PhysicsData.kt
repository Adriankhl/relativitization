package relativitization.universe.data.physics

import kotlinx.serialization.Serializable

@Serializable
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val fuelRestMass: Double = 1.0,
    val maxDeltaFuelRestMass: Double = 0.0,
) {
    fun totalRestMass() = coreRestMass + fuelRestMass
}

@Serializable
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var fuelRestMass: Double = 1.0,
    var maxDeltaFuelRestMass: Double = 0.0,
) {
    fun totalRestMass() = coreRestMass + fuelRestMass
}