package relativitization.universe.game.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.game.data.components.defaults.ai.FuelRestMassHistoryData
import relativitization.universe.game.data.components.defaults.ai.MutableFuelRestMassHistoryData

/**
 * Contain data for ai computation, not necessarily to be read by human
 *
 * @property aiTask determine the current task of ai
 * @property recentCommandTimeMap a map from player id to time, store the latest time this player
 *  sent a command to other player with that id, prevent repeatedly sending command due to
 *  observation delay
 * @property fuelRestMassHistoryData history of fuel rest mass
 */
@GenerateImmutable
@SerialName("AIData")
data class MutableAIData(
    var aiTask: AITask = AITask.DEFAULT,
    val recentCommandTimeMap: MutableMap<Int, Int> = mutableMapOf(),
    val fuelRestMassHistoryData: MutableFuelRestMassHistoryData = MutableFuelRestMassHistoryData(),
) : MutableDefaultPlayerDataComponent()

enum class AITask(val value: String) {
    DEFAULT("Default"),
    EMPTY("Empty"),
    ;

    override fun toString(): String {
        return value
    }
}

fun PlayerInternalData.aiData(): AIData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.aiData(): MutableAIData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.aiData(newAIData: MutableAIData) =
    playerDataComponentMap.put(newAIData)