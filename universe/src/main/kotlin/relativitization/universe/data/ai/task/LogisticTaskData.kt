package relativitization.universe.data.ai.task

import kotlinx.serialization.Serializable

@Serializable
data class LogisticTaskData(
    val targetPlayerId: Int = -1,
)

@Serializable
data class MutableLogisticTaskData(
    var targetPlayerId: Int = -1,
)