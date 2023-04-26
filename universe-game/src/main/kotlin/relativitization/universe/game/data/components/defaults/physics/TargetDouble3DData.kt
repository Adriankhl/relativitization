package relativitization.universe.game.data.components.defaults.physics

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.maths.physics.Double3D
import relativitization.universe.core.maths.physics.MutableDouble3D

/**
 * Move to this target position
 *
 * @property hasTarget whether this player should move to the target
 * @property commanderId the id of the player who ask this player to move to the position
 * @property target the target position
 * @property maxSpeed the maximum speed of the movement
 */
@GenerateImmutable
data class MutableTargetDouble3DData(
    var hasTarget: Boolean = false,
    var commanderId: Int = -1,
    var target: MutableDouble3D = MutableDouble3D(0.0, 0.0, 0.0),
    var maxSpeed: Double = 1.0,
)