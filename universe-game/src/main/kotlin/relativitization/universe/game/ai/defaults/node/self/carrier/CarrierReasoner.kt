package relativitization.universe.game.ai.defaults.node.self.carrier

import relativitization.universe.game.ai.defaults.consideration.building.SufficientLabourerEmploymentConsideration
import relativitization.universe.game.ai.defaults.consideration.carrier.NumberOfSpaceShipConsideration
import relativitization.universe.game.ai.defaults.consideration.fuel.SufficientProductionFuelConsideration
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.data.commands.BuildLocalCarrierCommand
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.game.data.components.defaults.science.application.MutableScienceApplicationData
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.playerScienceData
import kotlin.math.pow
import kotlin.random.Random

class CarrierReasoner(random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        CreateCarrierOption(),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
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
            ),
            SufficientLabourerEmploymentConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            ),
            NumberOfSpaceShipConsideration(
                initialMultiplier = 2.0,
                exponent = 0.5,
                rank = 1,
                bonus = 0.0
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
                qualityLevel = qualityLevel,
            )
        )
    }
}