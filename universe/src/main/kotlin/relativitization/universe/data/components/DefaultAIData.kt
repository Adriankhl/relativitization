package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name

@Serializable
@SerialName("AIData")
data class AIData(
    val aiTask: AITask = AITask.DEFAULT,
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("AIData")
data class MutableAIData(
    var aiTask: AITask = AITask.DEFAULT,
) : MutableDefaultPlayerDataComponent()

enum class AITask(val value: String) {
    DEFAULT("Default"),
    EMPTY("Empty"),
    ;

    override fun toString(): String {
        return value
    }
}
