package relativitization.universe.data.ai.task

import kotlinx.serialization.Serializable

@Serializable
data class LogisticsTaskData(
    val targetPlayerId: Int = -1,
)

@Serializable
data class MutableLogisticsTaskData(
    var targetPlayerId: Int = -1,
)