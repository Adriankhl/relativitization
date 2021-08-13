package relativitization.universe.data.ai

import kotlinx.serialization.Serializable
import relativitization.universe.data.ai.task.BuyResourceTask
import relativitization.universe.data.ai.task.LogisticTaskData
import relativitization.universe.data.ai.task.MutableBuyResourceTask
import relativitization.universe.data.ai.task.MutableLogisticTaskData

@Serializable
data class AIData(
    val aiName: String = "DefaultAI",
    val aiTask: AITask = AITask.DEFAULT,
    val logisticTaskData: LogisticTaskData = LogisticTaskData(),
    val buyResourceTask: BuyResourceTask = BuyResourceTask(),
)

@Serializable
data class MutableAIData(
    var aiName: String = "DefaultAI",
    var aiTask: AITask = AITask.DEFAULT,
    var logisticTaskData: MutableLogisticTaskData = MutableLogisticTaskData(),
    var buyResourceTask: MutableBuyResourceTask = MutableBuyResourceTask(),
)

enum class AITask(val value: String) {
    DEFAULT("Default"),
    LOGISTIC("Logistic"),
    BUY_RESOURCE("Buy resource"),
    ;

    override fun toString(): String {
        return value
    }
}
