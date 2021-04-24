package relativitization.universe.generate

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.*
import relativitization.universe.generate.fixed.Minimal

@Serializable
data class GenerateSetting(
    var generateMethod: String = "fixed-Minimal",
    var numPlayer: Int = 4,
    var numHumanPlayer: Int = 2,
    var numExtraStellarSystem: Int = 3,
    var universeSettings: MutableUniverseSettings = MutableUniverseSettings(),
)

@Serializable
abstract class GeneratedUniverse {
    abstract fun generate(setting: GenerateSetting): UniverseData


    companion object {
        private val logger = LogManager.getLogger()

        fun isSettingValid(setting: GenerateSetting): Boolean {
            val generateData = generate(setting)
            return if (generateData.isUniverseValid()) {
                true
            } else {
                val className = this::class.qualifiedName
                logger.error("$className: Generated universe is not valid")
                false
            }
        }

        fun generate(setting: GenerateSetting): UniverseData {
            return when(setting.generateMethod) {
                "fixed-Minimal" -> Minimal().generate(setting)
                else -> Minimal().generate(setting)
            }
        }
    }
}