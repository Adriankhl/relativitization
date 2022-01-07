package relativitization.universe.ai.defaults.consideration.fuel

import relativitization.universe.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.ai.defaults.utils.DualUtilityData
import relativitization.universe.ai.defaults.utils.PlanState
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.ai.MutableFuelRestMassHistoryData

class IncreasingProductionFuelConsideration : DualUtilityConsideration {
    override fun getDualUtilityData(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): DualUtilityData {
        val fuelHistory: MutableFuelRestMassHistoryData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.aiData().fuelRestMassHistoryData

        
    }
}