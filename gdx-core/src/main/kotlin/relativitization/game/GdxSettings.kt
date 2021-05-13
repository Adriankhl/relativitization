package relativitization.game

import kotlinx.serialization.Serializable

@Serializable
data class GdxSetting(
    var resolution: String = "1360x765",
    var continuousRendering: Boolean = false,
    var musicVolume: Float = 0.5f,
    var soundEffectsVolume: Float = 0.5f,
    var windowsWidth: Int = 1360,
    var windowsHeight: Int = 768,
    var smallFontScale: Float = 0.5f,
    var mediumFontScale: Float = 1.0f,
    var largeFontScale: Float = 1.5f,
)