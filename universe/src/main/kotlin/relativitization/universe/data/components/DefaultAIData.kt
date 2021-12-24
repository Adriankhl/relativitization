package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.ai.DefaultAI
import relativitization.universe.ai.name

/**
 * Contain data for ai computation, not necessarily to be read by human
 *
 * @property aiTask determine the current task of ai
 * @property recentCommandTimeMap a map from player id to time, store the latest time this player
 * sent a command to other player with that id, prevent repeatedly sending command due to
 * observation delay
 */
@Serializable
@SerialName("AIData")
data class AIData(
    val aiTask: AITask = AITask.DEFAULT,
    val recentCommandTimeMap: Map<Int, Int> = mapOf(),
) : DefaultPlayerDataComponent()

@Serializable
@SerialName("AIData")
data class MutableAIData(
    var aiTask: AITask = AITask.DEFAULT,
    val recentCommandTimeMap: MutableMap<Int, Int> = mutableMapOf(),
) : MutableDefaultPlayerDataComponent()

enum class AITask(val value: String) {
    DEFAULT("Default"),
    EMPTY("Empty"),
    ;

    override fun toString(): String {
        return value
    }
}
