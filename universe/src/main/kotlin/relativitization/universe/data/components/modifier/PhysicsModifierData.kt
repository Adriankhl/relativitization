package relativitization.universe.data.components.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

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
    fun updateByProperTime(gamma: Double) {
        // Pretend doing something
        gamma + 0.1
    }

    /**
     * Disable fuel increase (e.g., production, transfer) by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableFuelIncreaseByTime(time: Int) {
        disableRestMassIncreaseTimeLimit = max(disableRestMassIncreaseTimeLimit, time)
    }
}