package relativitization.universe.data

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.spacetime.MinkowskiSpacetime
import relativitization.universe.utils.RelativitizationLogManager

/**
 * Universe settings parameters
 *
 * @property universeName name of the universe
 * @property spacetimeCollectionName the name of the spacetime structure of the universe
 * @property commandCollectionName the name of the available command collection,
 *  or "All" to available all commands
 * @property mechanismCollectionName the name of the mechanism collection
 * @property globalMechanismCollectionName the name of global mechanism collection,
 *  such as how universe science is generated procedurally
 * @property speedOfLight speed of light in integer
 * @property xDim x dimension of the universe
 * @property yDim y dimension of the universe
 * @property zDim z dimension of the universe
 * @property tDim t dimension of the universe
 * @property universeBoundary the boundary condition of the universe
 * @property playerAfterImageDuration how long the player data should be stored after the
 *  player move
 * @property playerHistoricalInt4DLength how many int4D should be stored in PlayerData
 * @property groupEdgeLength the length of the smaller cube in a unit cube,
 *  players within the smaller cube communicate with zero time delay
 * @property randomSeed the master random seed for the universe simulation
 * @property otherIntMap a map from string to Int, to add custom parameter for specific model
 * @property otherDoubleMap a map from string to double, to add custom parameter for specific model
 * @property otherStringMap a map from string to string, to add custom parameter for specific model
 */
@Serializable
data class UniverseSettings(
    val universeName: String = "my universe",
    val spacetimeCollectionName: String = MinkowskiSpacetime.name(),
    val commandCollectionName: String = DefaultCommandAvailability.name(),
    val mechanismCollectionName: String = DefaultMechanismLists.name(),
    val globalMechanismCollectionName: String = DefaultGlobalMechanismList.name(),
    val speedOfLight: Double = 1.0,
    val xDim: Int = 3,
    val yDim: Int = 3,
    val zDim: Int = 3,
    val tDim: Int = Intervals.intDelay(
        Int3D(0, 0, 0),
        Int3D(xDim - 1, yDim - 1, zDim - 1),
        speedOfLight
    ) + 1,
    val universeBoundary: UniverseBoundary = UniverseBoundary.REFLECTIVE,
    val playerAfterImageDuration: Int = Intervals.maxDelayAfterMove(speedOfLight),
    val playerHistoricalInt4DLength: Int = playerAfterImageDuration,
    val groupEdgeLength: Double = 0.01,
    val randomSeed: Long = Clock.System.now().epochSeconds,
    val otherIntMap: Map<String, Int> = mapOf(),
    val otherDoubleMap: Map<String, Double> = mapOf(),
    val otherStringMap: Map<String, String> = mapOf(),
) {


    /**
     * Check if the setting is valid for Minkowski spacetime, always return true for other spacetime
     */
    fun isSettingValid(): Boolean {
        return if (spacetimeCollectionName == MinkowskiSpacetime.name()) {
            MinkowskiSpacetime.isUniverseSettingsValid(this)
        } else {
            true
        }
    }

    fun getOtherIntOrDefault(name: String, default: Int): Int {
        return otherIntMap.getOrElse(name) {
            logger.error("Missing other Int from universe settings, name: $name")
            default
        }
    }

    fun getOtherDoubleOrDefault(name: String, default: Double): Double {
        return otherDoubleMap.getOrElse(name) {
            logger.error("Missing other Double from universe settings, name: $name")
            default
        }
    }

    fun getOtherStringOrDefault(name: String, default: String): String {
        return otherStringMap.getOrElse(name) {
            logger.error("Missing other String from universe settings, name: $name")
            default
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}


@Serializable
data class MutableUniverseSettings(
    var universeName: String = "my universe",
    var spacetimeCollectionName: String = MinkowskiSpacetime.name(),
    var commandCollectionName: String = DefaultCommandAvailability.name(),
    var mechanismCollectionName: String = DefaultMechanismLists.name(),
    var globalMechanismCollectionName: String = DefaultGlobalMechanismList.name(),
    var speedOfLight: Double = 1.0,
    var xDim: Int = 3,
    var yDim: Int = 3,
    var zDim: Int = 3,
    var tDim: Int = Intervals.intDelay(
        Int3D(0, 0, 0),
        Int3D(xDim - 1, yDim - 1, zDim - 1),
        speedOfLight
    ) + 1,
    var universeBoundary: UniverseBoundary = UniverseBoundary.REFLECTIVE,
    var playerAfterImageDuration: Int = Intervals.maxDelayAfterMove(speedOfLight),
    var playerHistoricalInt4DLength: Int = playerAfterImageDuration,
    var groupEdgeLength: Double = 0.01,
    var randomSeed: Long = Clock.System.now().epochSeconds,
    val otherIntMap: MutableMap<String, Int> = mutableMapOf(),
    val otherDoubleMap: MutableMap<String, Double> = mutableMapOf(),
    val otherStringMap: MutableMap<String, String> = mutableMapOf(),
) {
    fun randomizeSeed() {
        randomSeed = Clock.System.now().epochSeconds
    }

    fun getOtherIntOrDefault(name: String, default: Int): Int {
        return otherIntMap.getOrElse(name) {
            logger.error("Missing other Int from universe settings, name: $name")
            default
        }
    }

    fun getOtherDoubleOrDefault(name: String, default: Double): Double {
        return otherDoubleMap.getOrElse(name) {
            logger.error("Missing other Double from universe settings, name: $name")
            default
        }
    }

    fun getOtherStringOrDefault(name: String, default: String): String {
        return otherStringMap.getOrElse(name) {
            logger.error("Missing other String from universe settings, name: $name")
            default
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

enum class UniverseBoundary(val value: String) {
    REFLECTIVE("Reflective"),
    ABSORPTIVE("Absorptive"),
    ;

    override fun toString(): String {
        return value
    }
}