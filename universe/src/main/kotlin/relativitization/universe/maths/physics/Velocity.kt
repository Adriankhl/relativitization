package relativitization.universe.maths.physics

import relativitization.universe.data.physics.Velocity
import kotlin.math.sqrt

fun gamma(velocity: Velocity, speedOfLight: Int = 1): Double {
    return 1.0 / sqrt(1.0 - velocity.squareMag() / (speedOfLight * speedOfLight).toDouble())
}