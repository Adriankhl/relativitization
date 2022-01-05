package relativitization.universe.ai.defaults.consideration.carrier

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer

/**
 * Check if the player has enough fuel to build a new spaceship
 */
class NewSpaceShipFuelConsideration : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        TODO("Not yet implemented")
    }
}