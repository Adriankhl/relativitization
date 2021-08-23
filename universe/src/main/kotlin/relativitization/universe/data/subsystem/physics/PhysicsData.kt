package relativitization.universe.data.subsystem.physics

import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property fuelRestMass the rest mass of the fuel, can be converted to energy to change velocity
 * @property maxDeltaFuelRestMass maximum change of fuel mass per turn
 */
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