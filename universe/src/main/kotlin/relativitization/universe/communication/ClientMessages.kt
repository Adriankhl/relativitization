package relativitization.universe.communication

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.Command
import relativitization.universe.generate.GenerateSetting

@Serializable
data class CreateUniverseMessage(
    val adminPassword: String,
    val generateSetting: GenerateSetting,
)

@Serializable
data class CommandInputMessage(
    val id: Int,
    val password: String,
    val commandList: List<Command>,
)
