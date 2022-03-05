package relativitization.game

import kotlinx.serialization.Serializable
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.utils.RelativitizationLogManager
import java.io.File
import kotlin.math.max

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
    var showingInfo: Boolean = true,
    var worldMapAndInfoSplitAmount: Float = 0.7f,
    var showingBottomCommand: Boolean = true,
    var upperInfoAndBottomCommandSplitAmount: Float = 0.8f,
    var showingUpperInfo: String = "Overview",
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
         * @param defaultScale scale the graphics by this value for default settings
         */
        fun loadOrDefault(
            programDir: String,
            defaultScale: Double,
        ): GdxSettings {
            return try {
                logger.debug("Trying to load gdx settings")
                // This can fail due to having older version of setting or file doesn't exist
                load(programDir)
            } catch (e: Throwable) {
                logger.debug("Load gdx settings fail, use default settings")
                val gdxSettings = GdxSettings()
                gdxSettings.smallFontSize = (gdxSettings.smallFontSize * defaultScale).toInt()
                gdxSettings.normalFontSize = (gdxSettings.normalFontSize * defaultScale).toInt()
                gdxSettings.bigFontSize = (gdxSettings.bigFontSize * defaultScale).toInt()
                gdxSettings.hugeFontSize = (gdxSettings.hugeFontSize * defaultScale).toInt()
                gdxSettings.maxFontSize = max(
                    72,
                    gdxSettings.hugeFontSize,
                )

                gdxSettings.imageScale = (gdxSettings.imageScale * defaultScale).toFloat()

                gdxSettings.worldMapAndInfoSplitAmount = (1.0 - (1.0 -
                        gdxSettings.worldMapAndInfoSplitAmount) * defaultScale).toFloat()

                gdxSettings.upperInfoAndBottomCommandSplitAmount = (1.0 - (1.0 -
                        gdxSettings.upperInfoAndBottomCommandSplitAmount) * defaultScale).toFloat()

                gdxSettings
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