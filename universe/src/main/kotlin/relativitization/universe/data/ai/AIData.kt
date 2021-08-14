package relativitization.universe.data.ai

import kotlinx.serialization.Serializable
import relativitization.universe.data.ai.task.BuyResourceTask
import relativitization.universe.data.ai.task.LogisticsTaskData
import relativitization.universe.data.ai.task.MutableBuyResourceTask
import relativitization.universe.data.ai.task.MutableLogisticsTaskData

@Serializable
data class AIData(
    val aiName: String = "DefaultAI",
    val aiTask: AITask = AITask.DEFAULT,
    val logisticsTaskData: LogisticsTaskData = LogisticsTaskData(),
    val buyResourceTask: BuyResourceTask = BuyResourceTask(),
)

@Serializable
data class MutableAIData(
    var aiName: String = "DefaultAI",
    var aiTask: AITask = AITask.DEFAULT,
    var logisticsTaskData: MutableLogisticsTaskData = MutableLogisticsTaskData(),
    var buyResourceTask: MutableBuyResourceTask = MutableBuyResourceTask(),
)

enum class AITask(val value: String) {
    DEFAULT("Default"),
    EMPTY("Empty"),
    LOGISTICS("Logistics"),
    BUY_RESOURCE("Buy resource"),
    ;

    override fun toString(): String {
        return value
    }
}
