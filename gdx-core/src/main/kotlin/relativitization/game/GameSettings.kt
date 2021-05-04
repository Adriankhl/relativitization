package relativitization.game

import kotlinx.serialization.Serializable


@Serializable
data class GameSetting(
    var resolution: String = "1360x765",
    var continuousRendering: Boolean = false,
    var musicVolume: Float = 0.5f,
    var soundEffectsVolume: Float = 0.5f,
)