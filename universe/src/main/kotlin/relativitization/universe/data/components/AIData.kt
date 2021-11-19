package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.ai.default.DefaultAI
import relativitization.universe.ai.name

@Serializable
@SerialName("AIData")
data class AIData(
    val aiName: String = DefaultAI.name(),
    val aiTask: AITask = AITask.DEFAULT,
) : PlayerDataComponent()

@Serializable
@SerialName("AIData")
data class MutableAIData(
    var aiName: String = DefaultAI.name(),
    var aiTask: AITask = AITask.DEFAULT,
) : MutablePlayerDataComponent()

enum class AITask(val value: String) {
    DEFAULT("Default"),
    EMPTY("Empty"),
    ;

    override fun toString(): String {
        return value
    }
}
