package relativitization.universe.generate.method

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutableUniverseSettings
import relativitization.universe.data.UniverseData
import relativitization.universe.data.serializer.DataSerializer.decode
import relativitization.universe.data.serializer.DataSerializer.encode
import relativitization.universe.generate.method.abm.ABMGenerateUniverseMethod
import relativitization.universe.generate.method.testing.TestingFixedMinimal
import relativitization.universe.generate.method.testing.TestingGenerateUniverseMethod
import relativitization.universe.utils.RelativitizationLogManager
import java.io.File

@Serializable
data class GenerateSettings(
    var generateMethod: String = TestingFixedMinimal.name(),
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

    val generateMethodList: List<GenerateUniverseMethod> =
        TestingGenerateUniverseMethod::class.sealedSubclasses.map {
            it.objectInstance!!
        } + ABMGenerateUniverseMethod::class.sealedSubclasses.map {
            it.objectInstance!!
        }

    // Store all generate method
    val generateMethodMap: Map<String, GenerateUniverseMethod> = generateMethodList.map {
        it.name() to it
    }.toMap()

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

        val newUniverseData: UniverseData = generateMethod.generate(settings)

        if (!newUniverseData.isUniverseValid()) {
            logger.error("Universe data is invalid")
            throw Error("Universe data is invalid")
        }

        return newUniverseData
    }
}