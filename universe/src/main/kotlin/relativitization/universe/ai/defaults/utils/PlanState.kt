package relativitization.universe.ai.defaults.utils

/**
 * For storing plan state for communication between nodes
 */
class PlanState(
    val interestPlayerId: MutableList<Int> = mutableListOf(),
)