package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.DefaultCommandAvailability
import relativitization.universe.data.commands.name
import relativitization.universe.global.DefaultGlobalMechanismList
import relativitization.universe.global.name
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.maths.physics.Intervals.maxDelayAfterMove
import relativitization.universe.mechanisms.DefaultMechanismLists
import relativitization.universe.mechanisms.name

/**
 * Setting data
 *
 * @property universeName name of the universe
 * @property mechanismCollectionName the name of the mechanism collection
 * @property commandCollectionName the name of the available command collection, or "All" to available all commands
 * @property globalMechanismCollectionName the name of global mechanism collection, such as how universe science is generated procedurally
 * @property speedOfLight speed of light in integer
 * @property tDim t dimension of the universe
 * @property xDim x dimension of the universe
 * @property yDim y dimension of the universe
 * @property zDim z dimension of the universe
 * @property playerAfterImageDuration how long the player data should be stored after the player move
 * @property playerHistoricalInt4DLength how many int4D should be stored in PlayerData
 * @property otherIntMap a map from string to Int, to add custom parameter for specific model
 * @property otherDoubleMap a map from string to double, to add custom parameter for specific model
 */
@Serializable
data class UniverseSettings(
    val universeName: String = "my universe",
    val commandCollectionName: String = DefaultCommandAvailability.name(),
    val mechanismCollectionName: String = DefaultMechanismLists.name(),
    val globalMechanismCollectionName: String = DefaultGlobalMechanismList.name(),
    val speedOfLight: Double = 1.0,
    val xDim: Int = 3,
    val yDim: Int = 3,
    val zDim: Int = 3,
    val tDim: Int = intDelay(Int3D(0, 0, 0), Int3D(xDim - 1, yDim - 1, zDim - 1), speedOfLight) + 1,
    val playerAfterImageDuration: Int = maxDelayAfterMove(speedOfLight),
    val playerHistoricalInt4DLength: Int = playerAfterImageDuration,
    val groupEdgeLength: Double = 0.01,
    val otherIntMap: Map<String, Int> = mapOf(),
    val otherDoubleMap: Map<String, Double> = mapOf(),
    val otherStringMap: Map<String, String> = mapOf(),
) {
    private fun isTDimBigEnough(): Boolean {
        return tDim > intDelay(Int3D(0, 0, 0), Int3D(xDim - 1, yDim - 1, zDim - 1), speedOfLight)
    }

    private fun isPlayerAfterImageDurationValid(): Boolean {
        return (playerAfterImageDuration >= maxDelayAfterMove(speedOfLight)) && (playerAfterImageDuration < tDim)
    }

    private fun isPlayerHistoricalInt4DLengthValid(): Boolean {
        return playerHistoricalInt4DLength >= playerAfterImageDuration
    }

    fun isSettingValid(): Boolean {
        return isPlayerAfterImageDurationValid() && isPlayerHistoricalInt4DLengthValid() && isTDimBigEnough()
    }
}


@Serializable
data class MutableUniverseSettings(
    var universeName: String = "my universe",
    var commandCollectionName: String = DefaultCommandAvailability.name(),
    var mechanismCollectionName: String = DefaultMechanismLists.name(),
    var globalMechanismCollectionName: String = DefaultGlobalMechanismList.name(),
    var speedOfLight: Double = 1.0,
    var xDim: Int = 3,
    var yDim: Int = 3,
    var zDim: Int = 3,
    var tDim: Int = intDelay(Int3D(0, 0, 0), Int3D(xDim - 1, yDim - 1, zDim - 1), speedOfLight) + 1,
    var playerAfterImageDuration: Int = maxDelayAfterMove(speedOfLight),
    var playerHistoricalInt4DLength: Int = playerAfterImageDuration,
    var groupEdgeLength: Double = 0.01,
    val otherIntMap: MutableMap<String, Int> = mutableMapOf(),
    val otherDoubleMap: MutableMap<String, Double> = mutableMapOf(),
    val otherStringMap: MutableMap<String, String> = mutableMapOf(),
)