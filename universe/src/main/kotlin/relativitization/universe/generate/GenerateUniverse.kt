package relativitization.universe.generate

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutableUniverseData4D
import relativitization.universe.data.UniverseData
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command

@Serializable
data class GenerateSetting(
    var universeName: String = "Test",
    var numPlayer: Int = 4,
    var speedOfLight: Int = 1,
    var numExtraStellarSystem: Int = 3,
    var tDim: Int = 8,
    var xDim: Int = 2,
    var yDim: Int = 2,
    var zDim: Int = 2,
    var playerAfterImageDuration: Int = 4,
    var playerHistoricalInt4DLength: Int = 4,
    var humanTimeLimit: Int = 600,
)

@Serializable
abstract class GeneratedUniverse {
    lateinit var universeData: UniverseData
    val generateSetting = GenerateSetting()
    var hasUniverseData: Boolean = false

    fun generateUniverseSettings(setting: GenerateSetting): UniverseSettings {
        return UniverseSettings(
            universeName = setting.universeName,
            speedOfLight = setting.speedOfLight,
            tDim = setting.tDim,
            xDim = setting.xDim,
            yDim = setting.yDim,
            zDim = setting.zDim,
            playerAfterImageDuration = setting.playerAfterImageDuration,
            playerHistoricalInt4DLength = setting.playerHistoricalInt4DLength,
            humanTimeLimit = setting.humanTimeLimit
        )
    }

    abstract fun generate(setting: GenerateSetting): UniverseData

    fun checkAndUpdate() {
        val generateData = generate(generateSetting)
        if (generateData.isUniverseValid()) {
            universeData = generateData
            hasUniverseData = true
        } else {
            val className = this::class.qualifiedName
            logger.error("$className: Generated universe is not valid")
        }
    }

    companion object {
        private val logger = LogManager.getLogger()
    }
}