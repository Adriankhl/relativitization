package relativitization.universe.data.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class PhysicsModifierData(
    val disableFuelIncreaseTimeLimit: Int = 0,
)

@Serializable
data class MutablePhysicsModifierData(
    var disableFuelIncreaseTimeLimit: Int = 0,
) {


    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        if (disableFuelIncreaseTimeLimit > 0) {
            disableFuelIncreaseTimeLimit -= 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime(gamma: Double) {

    }

    /**
     * Disable fuel increase (e.g., production, transfering) by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableFuelIncreaseByTime(time: Int) {
        disableFuelIncreaseTimeLimit = max(disableFuelIncreaseTimeLimit, time)
    }
}