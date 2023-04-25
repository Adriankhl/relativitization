package relativitization.universe.game.ai.defaults.node.self.carrier

import relativitization.universe.game.ai.defaults.consideration.carrier.NoSpaceShipConsideration
import relativitization.universe.game.ai.defaults.consideration.carrier.NumberOfSpaceShipConsideration
import relativitization.universe.game.ai.defaults.consideration.carrier.SufficientPopulationRatioConsideration
import relativitization.universe.game.ai.defaults.consideration.subordinate.NumberOfDirectSubordinateConsideration
import relativitization.universe.game.ai.defaults.utils.DoNothingDualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityConsideration
import relativitization.universe.game.ai.defaults.utils.DualUtilityOption
import relativitization.universe.game.ai.defaults.utils.DualUtilityReasoner
import relativitization.universe.game.ai.defaults.utils.PlanState
import relativitization.universe.core.data.PlanDataAtPlayer
import relativitization.universe.game.data.commands.SplitCarrierCommand
import relativitization.universe.game.data.components.MutablePopSystemData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.popsystem.CarrierType
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.utils.RelativitizationLogManager
import kotlin.random.Random

class SplitCarrierReasoner(private val random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        SplitCarrierOption(random),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
    )
}

class SplitCarrierOption(private val random: Random) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            NoSpaceShipConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
            ),
            SufficientPopulationRatioConsideration(
                ratio = 0.001,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.01,
                bonusIfFalse = 0.0
            ),
            NumberOfSpaceShipConsideration(
                initialMultiplier = 0.1,
                exponent = 2.0,
                rank = 0,
                bonus = 0.0
            ),
            NumberOfDirectSubordinateConsideration(
                initialMultiplier = 1.0,
                exponent = 0.5,
                rank = 0,
                bonus = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val popSystemData: MutablePopSystemData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData()

        // Filter carrier that is spaceship and has all factories
        val carrierIdList: List<Int> = popSystemData.carrierDataMap.filter { (_, carrierData) ->
            val isSpaceship: Boolean = carrierData.carrierType == CarrierType.SPACESHIP
            val hasFuelFactory: Boolean = carrierData.allPopData.labourerPopData.fuelFactoryMap
                .isNotEmpty()
            val hasAllResourceFactory: Boolean =
                ResourceType.factoryResourceList.all { resourceType ->
                    carrierData.allPopData.labourerPopData.resourceFactoryMap.values.any {
                        it.resourceFactoryInternalData.outputResource == resourceType
                    }
                }
            isSpaceship && hasFuelFactory && hasAllResourceFactory
        }.keys.toList().shuffled(random)

        // Only split if there are two valid carrier
        if (carrierIdList.size >= 2) {
            planDataAtPlayer.addCommand(
                SplitCarrierCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    carrierIdList = carrierIdList.take(1),
                    storageFraction = 0.3,
                )
            )
        } else {
            logger.debug("No suitable carrier to split")
        }
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}