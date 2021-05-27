package relativitization.universe.data

import kotlinx.serialization.Serializable
import relativitization.universe.maths.physics.Intervals.maxDelayAfterMove

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
 */
@Serializable
data class UniverseSettings(
    val universeName: String = "Test",
    val mechanismCollectionName: String = "DefaultMechanism",
    val commandCollectionName: String = "DefaultCommand",
    val speedOfLight: Double = 1.0,
    val tDim: Int = 8,
    val xDim: Int = 2,
    val yDim: Int = 2,
    val zDim: Int = 2,
    val playerAfterImageDuration: Int = 4,
    val playerHistoricalInt4DLength: Int = 4,
) {
    private fun isPlayerAfterImageDurationValid(): Boolean {
        return (playerAfterImageDuration >= maxDelayAfterMove(speedOfLight)) && (playerAfterImageDuration < tDim)
    }
    private fun isPlayerHistoricalInt4DLengthValid(): Boolean {
        return playerHistoricalInt4DLength >= playerAfterImageDuration
    }

    fun isSettingValid(): Boolean {
        return isPlayerAfterImageDurationValid() && isPlayerHistoricalInt4DLengthValid()
    }
}


@Serializable
data class MutableUniverseSettings(
    var universeName: String = "Test",
    var mechanismCollectionName: String = "DefaultMechanism",
    var commandCollectionName: String = "DefaultCommand",
    var speedOfLight: Double = 1.0,
    var tDim: Int = 8,
    var xDim: Int = 2,
    var yDim: Int = 2,
    var zDim: Int = 2,
    var playerAfterImageDuration: Int = 4,
    var playerHistoricalInt4DLength: Int = 4,
)