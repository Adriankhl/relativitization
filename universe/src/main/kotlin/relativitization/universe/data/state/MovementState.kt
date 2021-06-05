package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class MovementState(
    val targetToPlayerId: Int = -1
)

@Serializable
data class MutableMovementState(
    var targetToPlayerId: Int = -1
)