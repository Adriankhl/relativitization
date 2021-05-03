package relativitization.universe.communication

import kotlinx.serialization.Serializable

/**
 * Status message from server
 *
 * @property success whether the message is successfully returned from the server
 */
@Serializable
data class UniverseServerStatusMessage(
    val success: Boolean = false,
    val hasUniverse: Boolean = false,
    val runningUniverse: Boolean = false,
    val waitingInput: Boolean = false,
    val timeLeft: Long = 0,
    val currentUniverseTime: Int = -1
)