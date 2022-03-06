package relativitization.game

import kotlinx.serialization.Serializable
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager
import java.io.File

@Serializable
data class GdxSettings(
    var isContinuousRendering: Boolean = false,
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
    var isInfoPaneShow: Boolean = true,
    var worldMapInfoPaneSplitAmount: Float = 0.7f,
    var isBottomCommandInfoPaneShow: Boolean = true,
    var infoPaneSplitAmount: Float = 0.8f,
    var isControlBarTop: Boolean = true,
    var upperInfoPaneChoice: String = "Overview",
    var mapPlayerColorMode: MapPlayerColorMode = MapPlayerColorMode.ONE_COLOR_PER_PLAYER,
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

        /**
         * Load settings or create a default setting
         *
         * @param programDir load settings from this directory
         * @param default return this default if settings cannot be loaded
         */
        fun loadOrDefault(
            programDir: String,
            default: GdxSettings,
        ): GdxSettings {
            return try {
                logger.debug("Trying to load gdx settings")
                // This can fail due to having older version of setting or file doesn't exist
                load(programDir)
            } catch (e: Throwable) {
                default
            }
        }
    }
}

enum class MapPlayerColorMode(private val value: String) {
    ONE_COLOR_PER_PLAYER("One color per player"),
    TOP_LEADER("Top leader"),
    WAR_STATE("War state"),
    ;

    override fun toString(): String {
        return value
    }
}

enum class Language(private val value: String) {
    ENGLISH("English"),
    TRADITIONAL_CHINESE("Traditional Chinese (incomplete)"),
    SIMPLIFIED_CHINESE("Simplified Chinese (incomplete)"),
    ;

    override fun toString(): String {
        return value
    }
}