package relativitization.universe.communication

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateSetting

@Serializable
data class NewUniverseMessage(
    val adminPassword: String,
    val generateSetting: GenerateSetting,
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