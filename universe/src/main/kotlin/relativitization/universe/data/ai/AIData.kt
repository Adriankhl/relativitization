package relativitization.universe.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class AIData(
    val aiName: String = "DefaultAI"
)

@Serializable
data class MutableAIData(
    var aiName: String = "DefaultAI"
)
