package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class MovementState(
    val targetAttachId: Int = -1
)

@Serializable
data class MutableMovementState(
    var targetAttachId: Int = -1
)