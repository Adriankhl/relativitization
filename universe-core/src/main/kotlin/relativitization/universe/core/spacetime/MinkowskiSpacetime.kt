package relativitization.universe.core.spacetime

import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.maths.physics.Int3D
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.maths.physics.Relativistic
import relativitization.universe.core.maths.physics.Velocity

object MinkowskiSpacetime : Spacetime() {
    override fun computeTimeDelay(
        i1: Int3D,
        i2: Int3D,
        universeSettings: UniverseSettings
    ): Int {
        return Intervals.intDelay(
            c1 = i1,
            c2 = i2,
            speedOfLight = universeSettings.speedOfLight
        )
    }

    override fun computeDilatedTime(
        int3D: Int3D,
        velocity: Velocity,
        universeSettings: UniverseSettings
    ): Double {
        return Relativistic.dilatedTime(
            dt = 1.0,
            velocity = velocity,
            speedOfLight = universeSettings.speedOfLight
        )
    }

    private fun isTDimBigEnough(universeSettings: UniverseSettings): Boolean {
        return universeSettings.tDim > Intervals.intDelay(
            Int3D(0, 0, 0),
            Int3D(
                universeSettings.xDim - 1,
                universeSettings.yDim - 1,
                universeSettings.zDim - 1
            ),
            universeSettings.speedOfLight
        )
    }

    private fun isPlayerAfterImageDurationValid(universeSettings: UniverseSettings): Boolean {
        return (universeSettings.playerAfterImageDuration >= Intervals.maxDelayAfterMove(
            universeSettings.speedOfLight
        )) && (universeSettings.playerAfterImageDuration < universeSettings.tDim)
    }

    private fun isPlayerHistoricalInt4DLengthValid(universeSettings: UniverseSettings): Boolean {
        return universeSettings.playerHistoricalInt4DLength >=
                universeSettings.playerAfterImageDuration
    }

    fun isUniverseSettingsValid(universeSettings: UniverseSettings): Boolean {
        return isTDimBigEnough(universeSettings) &&
                isPlayerAfterImageDurationValid(universeSettings) &&
                isPlayerHistoricalInt4DLengthValid(universeSettings)
    }
}