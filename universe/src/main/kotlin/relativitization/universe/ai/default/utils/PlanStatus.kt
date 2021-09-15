package relativitization.universe.ai.default.utils

/**
 * For storing plan status for communication between nodes
 */
class PlanStatus(
    val interestPlayerId: MutableList<Int> = mutableListOf(),
)