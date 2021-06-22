package relativitization.universe.data.state

import kotlinx.serialization.Serializable

@Serializable
data class AIState(
    val aiName: String = "DefaultAI"
)

@Serializable
data class MutableAIState(
    var aiName: String = "DefaultAI"
)