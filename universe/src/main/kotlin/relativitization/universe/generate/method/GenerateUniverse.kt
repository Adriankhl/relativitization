package relativitization.universe.generate.method

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.generate.method.abm.ABMGenerateUniverseMethod
import relativitization.universe.generate.method.random.RandomGenerateUniverseMethod
import relativitization.universe.generate.method.random.RandomOneStarPerPlayerGenerate
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import relativitization.universe.generate.method.testing.TestingGenerateUniverseMethod
import relativitization.universe.utils.FileUtils
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Settings for universe generation, pass to GenerateUniverseMethod
 * Generation methods are not enforced to use any parameters here
 *
 * @property numPlayer number of initial players, human and AI
 * @property numHumanPlayer number of initial human players
 * @property numExtraStellarSystem number of "None" type player with a stellar system
 * @property initialPopulation initial population of the carrier
 */
@Serializable
data class GenerateSettings(
    var generateMethod: String = RandomOneStarPerPlayerGenerate.name(),
    var numPlayer: Int = 1,
    var numHumanPlayer: Int = 1,
    var numExtraStellarSystem: Int = 0,
    var initialPopulation: Double = 1E6,
    var universeSettings: MutableUniverseSettings = MutableUniverseSettings(),
) {
    fun save(programDir: String) {
        logger.debug("Saving generate setting to GenerateSettings.json")
        FileUtils.textToFile(
            text = encode(this),
            path = "$programDir/GenerateSettings.json"
        )
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()

        private fun load(programDir: String): GenerateSettings {
            val settingString: String = FileUtils.fileToText(
                "$programDir/GenerateSettings.json"
            )
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

/**
 * Base class of any method to generate universe data
 */
abstract class GenerateUniverseMethod {
    abstract fun generate(settings: GenerateSettings): UniverseData
}

fun GenerateUniverseMethod.name(): String = this::class.simpleName.toString()

/**
 * A collection of universe generation method
 */
object GenerateUniverseMethodCollection {
    private val logger = RelativitizationLogManager.getLogger()

    private val generateMethodList: List<GenerateUniverseMethod> =
        RandomGenerateUniverseMethod::class.sealedSubclasses.map {
            it.objectInstance!!
        } +
        TestingGenerateUniverseMethod::class.sealedSubclasses.map {
            it.objectInstance!!
        } + ABMGenerateUniverseMethod::class.sealedSubclasses.map {
            it.objectInstance!!
        }

    // Store all generate method
    val generateMethodMap: Map<String, GenerateUniverseMethod> = generateMethodList.associateBy {
        it.name()
    }

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
        val generateMethod: GenerateUniverseMethod =
            generateMethodMap.getOrElse(settings.generateMethod) {
                logger.error("Generate method doesn't exist, using default method")
                TestingFixedMinimal
            }

        return generateMethod.generate(settings)
    }
}