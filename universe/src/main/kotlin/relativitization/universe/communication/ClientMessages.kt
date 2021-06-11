package relativitization.universe.communication

import kotlinx.serialization.Serializable
import relativitization.universe.UniverseServerSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateSettings

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
data class CommandInputMessage(
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