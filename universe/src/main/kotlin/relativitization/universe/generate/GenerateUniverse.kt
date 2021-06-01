package relativitization.universe.generate

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.*
import relativitization.universe.generate.abm.Flocking
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
abstract class GenerateUniverse {
    abstract fun generate(setting: GenerateSetting): UniverseData


    companion object {
        private val logger = LogManager.getLogger()

        // Store all generate method
        val generateMethodMap: Map<String, GenerateUniverse> = mapOf(
            "fixed-Minimal" to Minimal(),
            "abm-flocking" to Flocking()
        )

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
            return if(generateMethodMap.keys.contains(setting.generateMethod)) {
                generateMethodMap.getValue(setting.generateMethod).generate(setting)
            } else {
                logger.error("Generate method doesn't exist, using default method")
                Minimal().generate(setting)
            }
        }
    }
}