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
     * Decrease remaining time for temporary state by 1
     */
    fun updateTimeRemain() {
        disableFuelProductionStateList.forEach {
            it.timeRemain -= 1
        }
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