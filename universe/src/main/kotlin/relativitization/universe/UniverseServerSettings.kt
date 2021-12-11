package relativitization.universe

import kotlinx.serialization.Serializable

/**
 * Settings of server, should be accessed with mutex lock to prevent setting and getting at the same time
 *
 * @property adminPassword the password for admin access
 * @property programDir the location of the program directories, "." for desktop and context.filesDir for android
 * @property clearInactivePerTurn clear the human player id and password and turn them to ai
 * @property waitTimeLimit time limit (in seconds) to wait for human input
 */
@Serializable
data class UniverseServerSettings(
    var adminPassword: String,
    val programDir: String = ".",
    var clearInactivePerTurn: Boolean = false,
    var waitTimeLimit: Int = 600,
)