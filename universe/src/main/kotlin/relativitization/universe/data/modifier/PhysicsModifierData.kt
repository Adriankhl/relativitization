package relativitization.universe.data.modifier

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
data class PhysicsModifierData(
    val disableFuelProductionTimeLimit: Int = 0,
)

@Serializable
data class MutablePhysicsModifierData(
    var disableFuelProductionTimeLimit: Int = 0,
) {


    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        if (disableFuelProductionTimeLimit > 0) {
            disableFuelProductionTimeLimit -= 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime(gamma: Double) {

    }

    /**
     * Disable fuel production by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableFuelProductionByTime(time: Int) {
        disableFuelProductionTimeLimit = max(disableFuelProductionTimeLimit, time)
    }
}