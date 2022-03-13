package relativitization.universe.ai.defaults.utils

/**
 * For storing plan state for communication between nodes
 *
 * @property foreignConstructionFuel fuel to construct foreign factories
 */
class PlanState(
    var foreignConstructionFuel: Double = 0.0,
)