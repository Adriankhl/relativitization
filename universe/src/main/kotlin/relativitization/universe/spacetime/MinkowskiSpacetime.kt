package relativitization.universe.spacetime

import relativitization.universe.data.UniverseSettings
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.maths.physics.Relativistic
import relativitization.universe.maths.physics.Velocity

object MinkowskiSpacetime : Spacetime() {
    override fun computeDelay(
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

    override fun computeDilation(
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
}