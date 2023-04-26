package relativitization.universe.game.data.components.defaults.physics

import ksergen.annotations.GenerateImmutable
import kotlin.math.min

/**
 * Fuel (in rest mass) of player for various purposes
 *
 * @property storage fuel for storage
 * @property movement fuel for movement
 * @property production fuel for production
 * @property trade fuel for production
 * @property maxMovementDelta maximum mass to use from movement per time
 */
@GenerateImmutable
data class MutableFuelRestMassData(
    var storage: Double = 0.0,
    var movement: Double = 0.0,
    var production: Double = 0.0,
    var trade: Double = 0.0,
    var maxMovementDelta: Double = 0.0,
)

fun FuelRestMassData.total(): Double = storage + movement + production + trade

fun MutableFuelRestMassData.total(): Double = storage + movement + production + trade

fun FuelRestMassData.maxMovementDeltaRestMass(): Double = min(movement, maxMovementDelta)

fun MutableFuelRestMassData.maxMovementDeltaRestMass(): Double = min(movement, maxMovementDelta)


/**
 * The target proportion of each category of fuel
 *
 * @property storage fuel for storage
 * @property movement fuel for movement
 * @property production fuel for production
 * @property trade fuel for production
 */
@GenerateImmutable
data class MutableFuelRestMassTargetProportionData(
    var storage: Double = 0.25,
    var movement: Double = 0.0,
    var production: Double = 0.5,
    var trade: Double = 0.25,
)

fun FuelRestMassTargetProportionData.total(): Double = storage + movement + production + trade

fun MutableFuelRestMassTargetProportionData.total(): Double = storage + movement + production + trade
