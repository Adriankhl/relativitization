package relativitization.universe.data.components.default.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

/**
 * Modifier related to physics
 *
 * @property disableRestMassIncreaseTimeLimit disable rest mass increase by this amount of turn
 */
@Serializable
data class PhysicsModifierData(
    val disableRestMassIncreaseTimeLimit: Int = 0,
)

@Serializable
data class MutablePhysicsModifierData(
    var disableRestMassIncreaseTimeLimit: Int = 0,
) {


    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        if (disableRestMassIncreaseTimeLimit > 0) {
            disableRestMassIncreaseTimeLimit -= 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime(gamma: Double) {  }

    /**
     * Disable fuel increase (e.g., production, transfer) by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableFuelIncreaseByTime(time: Int) {
        disableRestMassIncreaseTimeLimit = max(disableRestMassIncreaseTimeLimit, time)
    }
}