package relativitization.universe.communication

import kotlinx.serialization.Serializable

@Serializable
data class UniverseServerStatusMessage(
    val hasUniverse: Boolean,
    val runningUniverse: Boolean,
    val waitingInput: Boolean,
    val timeLeft: Long,
)