package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.consideration.fuel.IncreasingProductionFuelConsideration
import relativitization.universe.ai.defaults.consideration.fuel.PopulationSavingHighCompareToProductionConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeBaseSalaryCommand
import relativitization.universe.data.commands.ChangeSalaryFactorCommand
import relativitization.universe.data.components.*
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SalaryReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {

        val popSystemData: MutablePopSystemData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData()

        // Compute the map from carrier id to the order of the ratio
        // between population and ideal population for all carriers.
        // Carrier with lower ratio should have a higher salary to attract immigrant
        val populationRatioOrderMap: Map<Int, Int> = popSystemData.carrierDataMap.keys
            .sortedByDescending {
                val carrier: MutableCarrierData = popSystemData.carrierDataMap.getValue(it)
                if (carrier.carrierInternalData.idealPopulation > 0.0) {
                    carrier.allPopData.totalAdultPopulation() /
                            carrier.carrierInternalData.idealPopulation
                } else {
                    1.0
                }
            }.mapIndexed { index, i ->
                i to index
            }.toMap()

        val adjustSalaryFactorAINodeList: List<AdjustSalaryFactorAINode> = popSystemData.carrierDataMap
            .keys.map { carrierId ->
                AdjustSalaryFactorAINode(
                    carrierId,
                    populationRatioOrderMap,
                )
            }

        return adjustSalaryFactorAINodeList + listOf(
            AdjustBaseSalaryReasoner(random)
        )
    }
}

/**
 * Adjust the salary of pop in a carrier
 *
 * @property category the id of the carrier
 * @property populationRatioOrderMap a map from carrier id to the descending order of
 * population / ideal population among all carrier
 */
class AdjustSalaryFactorAINode(
    private val carrierId: Int,
    private val populationRatioOrderMap: Map<Int, Int>,
) : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Compute an factor determined by the order of population ratio to enhance migration
        val populationOrderBonus: Double = if (populationRatioOrderMap.isNotEmpty()) {
            val step: Double = 1.0 / populationRatioOrderMap.size
            step * populationRatioOrderMap.getValue(carrierId)
        } else {
            0.0
        }

        val carrier: MutableCarrierData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId)

        val totalCarrierPopulation: Double = carrier.allPopData.totalAdultPopulation()

        PopType.values().forEach { popType ->

            // Target to get even population for all type
            val targetPopulationFraction: Double = 1.0 / PopType.values().size

            val currentPopulationFraction: Double = if (totalCarrierPopulation > 0.0) {
                carrier.allPopData.getCommonPopData(
                    popType
                ).adultPopulation / totalCarrierPopulation
            } else {
                targetPopulationFraction
            }

            val targetPopulationBonus: Double = when {
                currentPopulationFraction < targetPopulationFraction -> 5.0
                else -> 0.0
            }

            val salaryFactor: Double = 1.0 + populationOrderBonus + targetPopulationBonus

            planDataAtPlayer.addCommand(
                ChangeSalaryFactorCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    carrierId = carrierId,
                    popType = popType,
                    salaryFactor = salaryFactor,
                )
            )
        }
    }
}

/**
 *
 */
class AdjustBaseSalaryReasoner(random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        IncreaseBaseSalaryOption(),
        DecreaseBaseSalaryOption(),
        DoNothingDualUtilityOption(
            rank = 1,
            multiplier = 1.0,
            bonus = 1.0,
        )
    )
}

/**
 * Increase salary if production fuel is increasing
 */
class IncreaseBaseSalaryOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            IncreasingProductionFuelConsideration(
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 0,
                multiplierIfFalse = 0.0,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Absolute minimum of salary
        val maxSalary = 1E10

        // Multiply this to get the new salary
        val salaryMultiplier = 1.25

        val physicsData: MutablePhysicsData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData()

        val currentBaseSalaryPerEmployee: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().generalPopSystemData.baseSalaryPerEmployee

        val totalAdultPopulation: Double = planState.totalAdultPopulation(planDataAtPlayer)

        // Bound the salary by production fuel
        val maxFuelAsBaseSalaryPerEmployee: Double = if (totalAdultPopulation > 0.0) {
            // Only use 0.05 of the production fuel as base salary
            0.05 * physicsData.fuelRestMassData.production / totalAdultPopulation
        } else {
            0.05 * physicsData.fuelRestMassData.production
        }

        val newBaseSalary: Double = min(
            min(maxSalary, maxFuelAsBaseSalaryPerEmployee),
            currentBaseSalaryPerEmployee * salaryMultiplier
        )

        planDataAtPlayer.addCommand(
            ChangeBaseSalaryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                baseSalaryPerEmployee = newBaseSalary,
            )
        )
    }
}


/**
 * Decrease salary if production fuel is decreasing
 */
class DecreaseBaseSalaryOption : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            IncreasingProductionFuelConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
            ),
            PopulationSavingHighCompareToProductionConsideration(
                productionFuelFactor = 0.5,
                rankIfTrue = 5,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 0,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.0
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // Absolute minimum of salary
        val minSalary = 1E-10

        // Multiply this to get the new salary
        val salaryMultiplier = 0.8


        val currentBaseSalaryPerEmployee: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().generalPopSystemData.baseSalaryPerEmployee

        val newBaseSalary: Double = max(
            minSalary,
            currentBaseSalaryPerEmployee * salaryMultiplier
        )

        planDataAtPlayer.addCommand(
            ChangeBaseSalaryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                baseSalaryPerEmployee = newBaseSalary,
            )
        )
    }
}