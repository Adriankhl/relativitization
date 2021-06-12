package relativitization.universe.data.state

import kotlinx.serialization.Serializable
import relativitization.universe.data.state.temporary.DisableFuelProductionState
import relativitization.universe.data.state.temporary.MutableDisableFuelProductionState

@Serializable
data class TemporaryState(
    val disableFuelProductionStateList: List<DisableFuelProductionState> = listOf()
)

@Serializable
data class MutableTemporaryState(
    val disableFuelProductionStateList: MutableList<MutableDisableFuelProductionState> = mutableListOf()
) {
    /**
     * Decrease remaining time
     */
    fun updateTimeRemain(gamma: Double) {
        updateByUniverseTime()
        updateByProperTime(gamma)
    }

    /**
     * Update the time by universe time
     */
    private fun updateByUniverseTime() {
        disableFuelProductionStateList.forEach {
            it.timeRemain -= 1
        }
    }


    /**
     * Update the time by proper (dilated) time of the player
     */
    private fun updateByProperTime(gamma: Double) {

    }

    /**
     * Clear old temporary state
     */
    fun clearOld() {
        disableFuelProductionStateList.removeAll {
            it.timeRemain <= 0
        }
    }
}