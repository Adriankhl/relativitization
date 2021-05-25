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
    var smallFontSize: Int = 16,
    var normalFontSize: Int = 24,
    var bigFontSize: Int = 30,
    var hugeFontSIze: Int = 40,
    var zoomFactor: Float = 1.2f,
    var imageScale: Float = 1.0f,
)