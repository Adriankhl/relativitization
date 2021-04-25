package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class AIState(
    val aiName: String = "EmptyAI"
)

@Serializable
data class MutableAIState(
    var aiName: String = "EmptyAI"
)