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
    fun save() {
        logger.debug("Saving generate setting to GenerateSetting.json")
        File("GenerateSetting.json").writeText(encode(this))
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        private fun load(): GenerateSettings {
            val settingString: String = File("GenerateSetting.json").readText()
            return decode(settingString)
        }

        fun loadOrDefault(): GenerateSettings {
            return try {
                logger.debug("Trying to load generate setting")
                // This can fail due to having older version of setting or file doesn't exist
                load()
            } catch (e: Throwable) {
                logger.debug("Load generate setting fail, use default setting")
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
            return if(generateMethodMap.keys.contains(settings.generateMethod)) {
                generateMethodMap.getValue(settings.generateMethod).generate(settings)
            } else {
                logger.error("Generate method doesn't exist, using default method")
                Minimal().generate(settings)
            }
        }
    }
}