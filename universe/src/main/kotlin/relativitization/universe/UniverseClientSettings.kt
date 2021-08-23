package relativitization.universe

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.physics.MutableInt3D

/**
 * Client settings
 *
 * @property programDir the location of the program directories, "." for desktop and context.filesDir for android
 * @property adminPassword admin password for accessing server
 * @property playerId id of the player
 * @property password player password, for input commands to server
 * @property serverAddress the url of server
 * @property serverPort port of the server
 * @property zLimit the z dimension limit of projected 2d universe data
 */
@Serializable
data class UniverseClientSettings(
    val programDir: String = ".",
    var adminPassword: String,
    var playerId: Int = -1,
    var password: String = "player password",
    var serverAddress: String = "127.0.0.1",
    var serverPort: String = "29979",
    var viewCenter: MutableInt3D = MutableInt3D(0, 0, 0),
    var zLimit:Int = 10,
)