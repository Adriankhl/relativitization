package relativitization.universe.game.data.components.defaults.modifier

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import kotlin.math.max

/**
 * Modifier related to physics
 *
 * @property disableRestMassIncreaseTimeLimit disable rest mass increase by this amount of turn
 */
@GenerateImmutable
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
    fun updateByProperTime() { }

    /**
     * Disable fuel increase (e.g., production, transfer) by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableFuelIncreaseByTime(time: Int) {
        disableRestMassIncreaseTimeLimit = max(disableRestMassIncreaseTimeLimit, time)
    }
}