package relativitization.universe.data.ai

import kotlinx.serialization.Serializable

@Serializable
data class AIData(
    val aiName: String = "DefaultAI",
    val aiTask: AITask = AITask.DEFAULT,
)

@Serializable
data class MutableAIData(
    var aiName: String = "DefaultAI",
    var aiTask: AITask = AITask.DEFAULT,
)

enum class AITask(val value: String) {
    DEFAULT("Default"),
    LOGISTIC("Logistic"),
    ;

    override fun toString(): String {
        return value
    }
}
