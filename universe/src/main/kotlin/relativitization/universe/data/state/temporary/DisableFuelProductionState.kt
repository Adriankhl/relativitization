package relativitization.universe.data.state.temporary

import kotlinx.serialization.Serializable

@Serializable
data class DisableFuelProductionState(
    val reason: String = "Disable fuel production",
    val timeRemain: Int = 0
)

@Serializable
data class MutableDisableFuelProductionState(
    var reason: String = "Disable fuel production",
    var timeRemain: Int = 0
)