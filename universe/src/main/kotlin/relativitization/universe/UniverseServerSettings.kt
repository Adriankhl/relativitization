package relativitization.universe

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

/**
 * Settings of server, should be accessed with mutex lock to prevent setting and getting at the same time
 *
 * @property adminPassword the password for admin access
 * @property clearInactivePerTurn clear the human player id and password and turn them to ai
 * @property waitTimeLimit time limit (in seconds) to wait for human input
 */
@Serializable
data class UniverseServerSettings(
    var adminPassword: String,
    var clearInactivePerTurn: Boolean = false,
    var waitTimeLimit: Int = 60,
)