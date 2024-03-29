package relativitization.universe.core.generate

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutableUniverseSettings
import relativitization.universe.core.data.UniverseData
import relativitization.universe.core.data.serializer.DataSerializer.decode
import relativitization.universe.core.data.serializer.DataSerializer.encode
import relativitization.universe.core.generate.empty.EmptyUniverse
import relativitization.universe.core.utils.FileUtils
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

/**
 * Settings for universe generation, pass to GenerateUniverseMethod
 * Generation methods are not enforced to use any parameters here
 *
 * @property numPlayer number of initial players, human and AI
 * @property numHumanPlayer number of initial human players
 * @property otherIntMap other integer parameters for generating universe
 * @property otherDoubleMap other double parameters for generating universe
 */
@Serializable
data class GenerateSettings(
    var generateMethod: String = EmptyUniverse.name(),
    var numPlayer: Int = 1,
    var numHumanPlayer: Int = 1,
    val otherIntMap: MutableMap<String, Int> = mutableMapOf(),
    val otherDoubleMap: MutableMap<String, Double> = mutableMapOf(),
    val otherStringMap: MutableMap<String, String> = mutableMapOf(),
    var universeSettings: MutableUniverseSettings = MutableUniverseSettings(),
) {
    fun save(programDir: String) {
        logger.debug("Saving generate setting to GenerateSettings.json")
        FileUtils.textToFile(
            text = encode(this),
            path = "$programDir/GenerateSettings.json"
        )
    }

    fun getOtherIntOrDefault(name: String, default: Int): Int {
        return otherIntMap.getOrElse(name) {
            logger.error("Missing other Int from generate settings, name: $name")
            default
        }
    }

    fun getOtherDoubleOrDefault(name: String, default: Double): Double {
        return otherDoubleMap.getOrElse(name) {
            logger.error("Missing other Double from generate settings, name: $name")
            default
        }
    }

    fun getOtherStringOrDefault(name: String, default: String): String {
        return otherStringMap.getOrElse(name) {
            logger.error("Missing other String from generate settings, name: $name")
            default
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        private fun load(programDir: String): GenerateSettings {
            val settingString: String = FileUtils.fileToText(
                "$programDir/GenerateSettings.json"
            )
            return decode(settingString)
        }

        fun loadOrDefault(programDir: String, default: GenerateSettings): GenerateSettings {
            return try {
                logger.debug("Trying to load generate settings")
                // This can fail due to having older version of setting or file doesn't exist
                load(programDir)
            } catch (e: Throwable) {
                logger.debug("Load generate settings fail, use default settings")
                default
            }
        }
    }
}

/**
 * Base class of any method to generate universe data
 */
abstract class GenerateUniverseMethod {
    open fun name(): String = this::class.simpleName.toString()

    abstract fun generate(
        generateSettings: GenerateSettings,
        random: Random,
    ): UniverseData
}

/**
 * A collection of universe generation method
 */
object GenerateUniverseMethodCollection {
    private val logger = RelativitizationLogManager.getLogger()

    // Store all generate method
    private val generateUniverseMethodNameMap: MutableMap<String, GenerateUniverseMethod> =
        mutableMapOf(
            EmptyUniverse.name() to EmptyUniverse
        )

    fun getGenerateUniverseMethodNames(): Set<String> = generateUniverseMethodNameMap.keys

    fun addGenerateUniverseMethod(generateUniverseMethod: GenerateUniverseMethod) {
        val generateUniverseMethodName: String = generateUniverseMethod.name()
        if (generateUniverseMethodNameMap.containsKey(generateUniverseMethodName)) {
            logger.debug(
                "Already has $generateUniverseMethodName in GenerateUniverseMethodCollection, " +
                        "replacing stored $generateUniverseMethodName"
            )
        }

        generateUniverseMethodNameMap[generateUniverseMethodName] = generateUniverseMethod
    }

    fun isSettingValid(generateSettings: GenerateSettings): Boolean {
        val generateData: UniverseData = generate(generateSettings)
        return if (generateData.isUniverseValid()) {
            true
        } else {
            val className = this::class.qualifiedName
            logger.error("$className: Generated universe is not valid")
            false
        }
    }

    fun generate(generateSettings: GenerateSettings): UniverseData {
        val generateMethod: GenerateUniverseMethod =
            generateUniverseMethodNameMap.getOrElse(generateSettings.generateMethod) {
                logger.error("Generate method doesn't exist, generate an empty universe")
                EmptyUniverse
            }

        return generateMethod.generate(
            generateSettings,
            Random(generateSettings.universeSettings.randomSeed)
        )
    }
}