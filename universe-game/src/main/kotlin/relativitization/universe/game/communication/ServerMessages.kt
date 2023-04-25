package relativitization.universe.game.communication

import kotlinx.serialization.Serializable

/**
 * Status message from server
 *
 * @property success whether the message is successfully returned from the server
 */
@Serializable
data class UniverseServerStatusMessage(
    val universeName: String,
    val success: Boolean = false,
    val hasUniverse: Boolean = false,
    val isUniverseRunning: Boolean = false,
    val isServerWaitingInput: Boolean = false,
    val timeLeft: Long = 0,
    val currentUniverseTime: Int = -1,
)