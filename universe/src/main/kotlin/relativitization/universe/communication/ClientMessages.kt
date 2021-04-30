package relativitization.universe.communication

import kotlinx.serialization.Serializable
import relativitization.universe.generate.GenerateSetting

@Serializable
data class CreateUniverseMessage(
    val adminPassword: String,
    val generateSetting: GenerateSetting,
)
