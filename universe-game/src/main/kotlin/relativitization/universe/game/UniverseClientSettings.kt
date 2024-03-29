package relativitization.universe.game

import kotlinx.serialization.Serializable
import relativitization.universe.core.maths.physics.MutableInt3D

/**
 * Client settings
 *
 * @property programDir the location of the program directories,
 *  "." for desktop and context.filesDir for android
 * @property adminPassword admin password for accessing server
 * @property playerId id of the player
 * @property password player password, for input commands to server
 * @property serverAddress the url of server
 * @property serverPort port of the server
 * @property zLimit the z dimension limit of projected 2d universe data
 * @property maxStoredUniverseData3DAtPlayer the maximum number of stored 3D data in client
 * @property autoConfirmCurrentCommand auto-confirm current command
 * @property httpRequestTimeout http request return from server time limit
 * @property httpConnectTimeout http find server time limit
 */
@Serializable
data class UniverseClientSettings(
    val programDir: String = ".",
    var adminPassword: String,
    var playerId: Int = -1,
    var password: String = "player password",
    var serverAddress: String = "127.0.0.1",
    var serverPort: Int = 29979,
    var viewCenter: MutableInt3D = MutableInt3D(0, 0, 0),
    var zLimit: Int = 10,
    var maxStoredUniverseData3DAtPlayer: Int = 10,
    var autoConfirmCurrentCommand: Boolean = true,
    var httpRequestTimeout: Long = 100000,
    var httpConnectTimeout: Long = 1000,
)