package relativitization.universe.ai.default.utils

/**
 * For storing plan state for communication between nodes
 */
class PlanState(
    val interestPlayerId: MutableList<Int> = mutableListOf(),
)