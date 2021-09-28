package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.data.commands.DefaultAvailableCommands
import relativitization.universe.data.commands.name
import relativitization.universe.data.component.physics.Int3D
import relativitization.universe.maths.physics.Intervals.intDelay
import relativitization.universe.maths.physics.Intervals.maxDelayAfterMove
import relativitization.universe.mechanisms.DefaultMechanismList
import relativitization.universe.mechanisms.name
import relativitization.universe.science.default.DefaultUniverseScienceDataProcess
import relativitization.universe.science.name

/**
 * Setting data
 *
 * @property universeName name of the universe
 * @property speedOfLight speed of light in integer
 * @property tDim t dimension of the universe
 * @property xDim x dimension of the universe
 * @property yDim y dimension of the universe
 * @property zDim z dimension of the universe
 * @property playerAfterImageDuration how long the player data should be stored after the player move
 * @property playerHistoricalInt4DLength how many int4D should be stored in PlayerData
 * @property otherSettings a map from string to string, to add custom parameter for specific model/AI/process
 */
@Serializable
data class UniverseSettings(
    val universeName: String = "Test",
    val mechanismCollectionName: String = DefaultMechanismList.name(),
    val commandCollectionName: String = DefaultAvailableCommands.name(),
    val universeScienceDataProcessCollectionName: String = DefaultUniverseScienceDataProcess.name(),
    val speedOfLight: Double = 1.0,
    val tDim: Int = 8,
    val xDim: Int = 2,
    val yDim: Int = 2,
    val zDim: Int = 2,
    val playerAfterImageDuration: Int = 4,
    val playerHistoricalInt4DLength: Int = 4,
    val groupEdgeLength: Double = 0.01,
    val otherSettings: Map<String, String> = mapOf(),
) {
    private fun isTDimBigEnough(): Boolean {
        return tDim > intDelay(Int3D(0, 0,0), Int3D(xDim - 1, yDim - 1, zDim - 1), speedOfLight)
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
    var universeName: String = "Test",
    var mechanismCollectionName: String = DefaultMechanismList.name(),
    var commandCollectionName: String = DefaultAvailableCommands.name(),
    var universeScienceDataProcessCollectionName: String = DefaultUniverseScienceDataProcess.name(),
    var speedOfLight: Double = 1.0,
    var tDim: Int = 8,
    var xDim: Int = 2,
    var yDim: Int = 2,
    var zDim: Int = 2,
    var playerAfterImageDuration: Int = 4,
    var playerHistoricalInt4DLength: Int = 4,
    var groupEdgeLength: Double = 0.01,
    var otherSettings: MutableMap<String, String> = mutableMapOf()
)