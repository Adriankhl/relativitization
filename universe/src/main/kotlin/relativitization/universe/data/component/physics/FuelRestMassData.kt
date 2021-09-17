package relativitization.universe.data.component.physics

import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * Fuel (in rest mass) of player for various purposes
 *
 * @property movement fuel for movement
 * @property production fuel for production
 * @property trade fuel for production
 */
@Serializable
data class FuelRestMassData(
    val movement: Double = 1.0,
    val production: Double = 1.0,
    val trade: Double = 1.0,
    val maxMovementDelta: Double = 0.0,
) {
    fun total(): Double = movement + production + trade
    fun maxMovementDeltaRestMass(): Double = min(movement, maxMovementDelta)
}

@Serializable
data class MutableFuelRestMassData(
    var movement: Double = 1.0,
    var production: Double = 1.0,
    var trade: Double = 1.0,
    var maxMovementDelta: Double = 0.0,
) {
    fun total(): Double = movement + production + trade
    fun maxMovementDeltaRestMass(): Double = min(movement, maxMovementDelta)
}