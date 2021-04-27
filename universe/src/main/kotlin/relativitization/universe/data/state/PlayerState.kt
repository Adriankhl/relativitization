package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val aiState: AIState = AIState(),
    val movementState: MovementState = MovementState()
)

@Serializable
data class MutablePlayerState(
    val aiState: MutableAIState = MutableAIState(),
    val movementState: MutableMovementState = MutableMovementState()
)