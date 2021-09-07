package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.ai.default.DefaultAI
import relativitization.universe.ai.name
import relativitization.universe.data.component.ai.task.BuyResourceTask
import relativitization.universe.data.component.ai.task.LogisticsTaskData
import relativitization.universe.data.component.ai.task.MutableBuyResourceTask
import relativitization.universe.data.component.ai.task.MutableLogisticsTaskData

@Serializable
@SerialName("AIData")
data class AIData(
    val aiName: String = DefaultAI.name(),
    val aiTask: AITask = AITask.DEFAULT,
    val logisticsTaskData: LogisticsTaskData = LogisticsTaskData(),
    val buyResourceTask: BuyResourceTask = BuyResourceTask(),
) : PlayerDataComponent()

@Serializable
@SerialName("AIData")
data class MutableAIData(
    var aiName: String = DefaultAI.name(),
    var aiTask: AITask = AITask.DEFAULT,
    var logisticsTaskData: MutableLogisticsTaskData = MutableLogisticsTaskData(),
    var buyResourceTask: MutableBuyResourceTask = MutableBuyResourceTask(),
) : MutablePlayerDataComponent()

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
