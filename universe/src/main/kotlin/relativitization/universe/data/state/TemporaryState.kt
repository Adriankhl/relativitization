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
)