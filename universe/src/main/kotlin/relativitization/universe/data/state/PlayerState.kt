package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class PlayerState(
    val aiState: AIState = AIState(),
    val temporaryState: TemporaryState = TemporaryState(),
)

@Serializable
data class MutablePlayerState(
    val aiState: MutableAIState = MutableAIState(),
    val temporaryState: MutableTemporaryState = MutableTemporaryState(),
)