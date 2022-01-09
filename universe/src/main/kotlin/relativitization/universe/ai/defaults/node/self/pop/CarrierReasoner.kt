package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.consideration.building.SufficientLabourerEmploymentConsideration
import relativitization.universe.ai.defaults.consideration.carrier.SufficientPopulationRatioConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.components.defaults.economy.ResourceType

class CarrierReasoner : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        CreateCarrierOption(),
        DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0)
    )
}

/**
 * Create a carrier for this player
 */
class CreateCarrierOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        // Fuel needed to build a low quality carrier2
        val fuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .playerScienceData().playerScienceApplicationData.newSpaceshipFuelNeededByConstruction(
                0.1
            )

        return listOf(
            SufficientPopulationRatioConsideration(
                ratio = 0.1,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0
            ),
            SufficientLabourerEmploymentConsideration(
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.1,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            SufficientProductionFuelConsideration(
                requiredProductionFuelRestMass = fuelNeeded,
                rankIfTrue = 1,
                multiplierIfTrue = 10.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
    }
}