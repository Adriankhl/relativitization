package relativitization.universe.generate

import kotlinx.serialization.Serializable
import relativitization.universe.data.*
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.generate.abm.FlockingGenerate
import relativitization.universe.generate.fixed.Minimal
import relativitization.universe.utils.RelativitizationLogManager
import java.io.File

@Serializable
data class GenerateSettings(
    var generateMethod: String = "FixedMinimal",
    var numPlayer: Int = 4,
    var numHumanPlayer: Int = 2,
    var numExtraStellarSystem: Int = 3,
    var universeSettings: MutableUniverseSettings = MutableUniverseSettings(),
) {
    fun save(programDir: String) {
        logger.debug("Saving generate setting to GenerateSettings.json")
        File("$programDir/GenerateSettings.json").writeText(encode(this))
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        private fun load(programDir: String): GenerateSettings {
            val settingString: String = File("$programDir/GenerateSettings.json").readText()
            return decode(settingString)
        }

        fun loadOrDefault(programDir: String): GenerateSettings {
            return try {
                logger.debug("Trying to load generate settings")
                // This can fail due to having older version of setting or file doesn't exist
                load(programDir)
            } catch (e: Throwable) {
                logger.debug("Load generate settings fail, use default settings")
                GenerateSettings()
            }
        }
    }
}

@Serializable
abstract class GenerateUniverse {
    abstract fun generate(settings: GenerateSettings): UniverseData


    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        // Store all generate method
        val generateMethodMap: Map<String, GenerateUniverse> = mapOf(
            "FixedMinimal" to Minimal(),
            "ABMFlocking" to FlockingGenerate()
        )

        fun isSettingValid(settings: GenerateSettings): Boolean {
            val generateData = generate(settings)
            return if (generateData.isUniverseValid()) {
                true
            } else {
                val className = this::class.qualifiedName
                logger.error("$className: Generated universe is not valid")
                false
            }
        }

        fun generate(settings: GenerateSettings): UniverseData {
            val generateMethod: GenerateUniverse =  generateMethodMap.getOrElse(settings.generateMethod) {
                logger.error("Generate method doesn't exist, using default method")
                Minimal()
            }

            return generateMethod.generate(settings)
        }
    }
}