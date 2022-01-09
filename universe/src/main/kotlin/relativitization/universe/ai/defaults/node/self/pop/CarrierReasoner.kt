package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.consideration.building.SufficientLabourerEmploymentConsideration
import relativitization.universe.ai.defaults.consideration.carrier.SufficientPopulationRatioConsideration
import relativitization.universe.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.BuildLocalCarrierCommand
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.science.application.MutableScienceApplicationData
import kotlin.math.pow

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
        // Fuel needed to build a highest quality ship
        val maxFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newSpaceshipFuelNeededByConstruction(
                1.0
            )

        // Fuel needed to build a lowest quality ship
        val minFuelNeeded: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.playerScienceData().playerScienceApplicationData
            .newSpaceshipFuelNeededByConstruction(
                0.0
            )

        return listOf(
            SufficientPopulationRatioConsideration(
                ratio = 0.1,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0
            ),
            SufficientLabourerEmploymentConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            SufficientProductionFuelConsideration(
                requiredProductionFuelRestMass = maxFuelNeeded,
                rankIfTrue = 1,
                multiplierIfTrue = 10.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 0.1,
                bonusIfFalse = 1.0
            ),
            SufficientProductionFuelConsideration(
                requiredProductionFuelRestMass = minFuelNeeded,
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {

        val scienceApplicationData: MutableScienceApplicationData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData
            .playerScienceData().playerScienceApplicationData

        val fuelData: MutableFuelRestMassData = planDataAtPlayer
            .getCurrentMutablePlayerData().playerInternalData.physicsData().fuelRestMassData

        // Find the suitable quality to build the new spaceship
        val qualityLevel: Double = (0..20).map {
            1.0 / 2.0.pow(it)
        }.firstOrNull {
            fuelData.production > scienceApplicationData.newSpaceshipFuelNeededByConstruction(it)
        } ?: 0.0

        // Search for quality that
        planDataAtPlayer.addCommand(
            BuildLocalCarrierCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                qualityLevel = qualityLevel,
            )
        )
    }
}