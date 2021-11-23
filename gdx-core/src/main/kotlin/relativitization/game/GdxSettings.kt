package relativitization.game

import kotlinx.serialization.Serializable
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager
import java.io.File

@Serializable
data class GdxSettings(
    var continuousRendering: Boolean = false,
    var windowsWidth: Int = 1360,
    var windowsHeight: Int = 768,
    var musicVolume: Float = 0.5f,
    var soundEffectsVolume: Float = 0.5f,
    var smallFontSize: Int = 16,
    var normalFontSize: Int = 24,
    var bigFontSize: Int = 32,
    var hugeFontSize: Int = 40,
    var maxFontSize: Int = 72,
    var mapZoomFactor: Float = 1.2f,
    var mapZoomRelativeToFullMap: Float = 1.0f,
    var imageScale: Float = 1.0f,
    var knowledgeMapZoomRelativeToFullMap: Float = 1.0f,
    var knowledgeMapProjectIconZoom: Float = 1.0f,
    var showingInfo: Boolean = true,
    var worldMapAndInfoSplitAmount: Float = 0.7f,
    var showingBottomCommand: Boolean = true,
    var upperInfoAndBottomCommandSplitAmount: Float = 0.8f,
    var showingInfoType: ShowingInfoType = ShowingInfoType.OVERVIEW,
    var language: Language = Language.ENGLISH,
) {
    fun save(programDir: String) {
        logger.debug("Saving gdx settings to GdxSettings.json")
        File("$programDir/GdxSettings.json").writeText(DataSerializer.encode(this))
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        private fun load(programDir: String): GdxSettings {
            val settingString: String = File("$programDir/GdxSettings.json").readText()
            return DataSerializer.decode(settingString)
        }

        fun loadOrDefault(programDir: String): GdxSettings {
            return try {
                logger.debug("Trying to load gdx settings")
                // This can fail due to having older version of setting or file doesn't exist
                load(programDir)
            } catch (e: Throwable) {
                logger.debug("Load gdx settings fail, use default settings")
                GdxSettings()
            }
        }
    }
}

enum class ShowingInfoType {
    PLAYERS,
    OVERVIEW,
    PHYSICS,
    EVENTS,
    COMMANDS,
    KNOWLEDGE_MAP,
    SCIENCE,
}

enum class Language(private val value: String) {
    ENGLISH("English"),
    TRADITIONAL_CHINESE("Traditional Chinese"),
    SIMPLIFIED_CHINESE("Simplified Chinese"),
    ;

    override fun toString(): String {
        return value
    }
}