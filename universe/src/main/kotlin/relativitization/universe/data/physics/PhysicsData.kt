package relativitization.universe.data.physics

import kotlinx.serialization.Serializable
import kotlin.math.min

@Serializable
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val fuelRestMass: Double = 1.0,
    val maxDeltaFuelRestMass: Double = 0.0,
) {
    fun totalRestMass() = coreRestMass + fuelRestMass
    fun maxDeltaRestMass() = min(fuelRestMass, maxDeltaFuelRestMass)
}

@Serializable
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var fuelRestMass: Double = 1.0,
    var maxDeltaFuelRestMass: Double = 0.0,
) {
    fun totalRestMass() = coreRestMass + fuelRestMass
    fun maxDeltaRestMass() = min(fuelRestMass, maxDeltaFuelRestMass)
}