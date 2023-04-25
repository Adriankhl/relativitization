package relativitization.universe.game.communication

import kotlinx.serialization.Serializable
import relativitization.universe.game.UniverseServerSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.generate.GenerateSettings

@Serializable
data class NewUniverseMessage(
    val adminPassword: String,
    val generateSettings: GenerateSettings,
)

@Serializable
data class LoadUniverseMessage(
    val adminPassword: String,
    val universeName: String
)

@Serializable
data class PlayerInputMessage(
    val id: Int,
    val password: String,
    val commandList: List<Command>,
)

@Serializable
data class RegisterPlayerMessage(
    val id: Int,
    val password: String
)

@Serializable
data class DeregisterPlayerMessage(
    val id: Int,
    val password: String
)

@Serializable
data class RunUniverseMessage(
    val adminPassword: String,
)

@Serializable
data class StopUniverseMessage(
    val adminPassword: String,
)

@Serializable
data class UniverseData3DMessage(
    val id: Int,
    val password: String,
)

@Serializable
data class CheckIsPlayerDeadMessage(
    val id: Int,
    val password: String,
)

@Serializable
data class UniverseServerSettingsMessage(
    val adminPassword: String,
    val universeServerSettings: UniverseServerSettings
)

@Serializable
data class StopWaitingMessage(
    val adminPassword: String
)