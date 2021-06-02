package relativitization.universe.generate

import kotlinx.serialization.Serializable
import org.apache.logging.log4j.LogManager
import relativitization.universe.data.*
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.generate.abm.Flocking
import relativitization.universe.generate.fixed.Minimal
import java.io.File

@Serializable
data class GenerateSetting(
    var generateMethod: String = "fixed-Minimal",
    var numPlayer: Int = 4,
    var numHumanPlayer: Int = 2,
    var numExtraStellarSystem: Int = 3,
    var universeSettings: MutableUniverseSettings = MutableUniverseSettings(),
) {
    fun save() {
        File("GenerateSetting.json").writeText(encode(this))
    }

    companion object {
        private val logger = LogManager.getLogger()

        private fun load(): GenerateSetting {
            val settingString: String = File("GenerateSetting.json").readText()
            return decode(settingString)
        }

        fun loadOrDefault(): GenerateSetting {
            return try {
                logger.debug("Trying to load generate setting")
                // This can fail due to having older version of setting or file doesn't exist
                load()
            } catch (e: Throwable) {
                logger.debug("Load generate setting fail, use default setting")
                GenerateSetting()
            }
        }
    }
}

@Serializable
abstract class GenerateUniverse {
    abstract fun generate(setting: GenerateSetting): UniverseData


    companion object {
        private val logger = LogManager.getLogger()

        // Store all generate method
        val generateMethodMap: Map<String, GenerateUniverse> = mapOf(
            "FixedMinimal" to Minimal(),
            "ABMFlocking" to Flocking()
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