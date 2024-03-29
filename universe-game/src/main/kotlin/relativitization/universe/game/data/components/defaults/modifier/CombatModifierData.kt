package relativitization.universe.game.data.components.defaults.modifier

import ksergen.annotations.GenerateImmutable
import kotlin.math.max

/**
 * Modifier related to combat
 *
 * @property disableMilitaryBaseRecoveryTimeLimit disable military base recovery by this
 *  number of turn
 */
@GenerateImmutable
data class MutableCombatModifierData(
    var disableMilitaryBaseRecoveryTimeLimit: Int = 0,
) {

    /**
     * Update the time by universe time
     */
    fun updateByUniverseTime() {
        if (disableMilitaryBaseRecoveryTimeLimit > 0) {
            disableMilitaryBaseRecoveryTimeLimit -= 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    fun updateByProperTime() {}

    /**
     * Disable fuel increase (e.g., production, transfer) by time
     *
     * @param time the minimum time to disable fuel production
     */
    fun disableMilitaryBaseRecoveryByTime(time: Int) {
        disableMilitaryBaseRecoveryTimeLimit = max(disableMilitaryBaseRecoveryTimeLimit, time)
    }
}