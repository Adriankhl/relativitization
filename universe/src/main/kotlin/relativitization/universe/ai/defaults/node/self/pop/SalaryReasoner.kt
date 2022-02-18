package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.consideration.fuel.IncreasingProductionFuelConsideration
import relativitization.universe.ai.defaults.consideration.fuel.PopulationSavingHighCompareToProduction
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeSalaryCommand
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.MutablePopSystemData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.popsystem.MutableCarrierData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import relativitization.universe.data.components.economyData
import kotlin.math.max
import kotlin.math.min

class SalaryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {

        val popSystemData: MutablePopSystemData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData()

        val totalAdultPopulation: Double = popSystemData.totalAdultPopulation()

        // Compute the order of the ratio between population and ideal population for all carriers.
        // Carrier with lower ratio should have a higher salary to attract immigrant
        val populationRatioOrder: List<Int> = popSystemData.carrierDataMap.keys.sortedByDescending {
            val carrier: MutableCarrierData = popSystemData.carrierDataMap.getValue(it)
            if (carrier.carrierInternalData.idealPopulation > 0.0) {
                carrier.allPopData.totalAdultPopulation() /
                        carrier.carrierInternalData.idealPopulation
            } else {
                1.0
            }
        }

        val adjustSalaryReasonerList: List<AdjustSalaryReasoner> = popSystemData.carrierDataMap
            .keys.map { carrierId ->
                PopType.values().map { popType ->
                    AdjustSalaryReasoner(
                        carrierId,
                        popType,
                        totalAdultPopulation,
                        populationRatioOrder,
                    )
                }
            }.flatten()

        return adjustSalaryReasonerList
    }
}

class AdjustSalaryReasoner(
    private val carrierId: Int,
    private val popType: PopType,
    private val totalAdultPopulation: Double,
    private val populationRatioOrder: List<Int>,
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        IncreaseSalaryOption(carrierId, popType, totalAdultPopulation),
        DecreaseSalaryOption(carrierId, popType),
        GoodSalaryOption(carrierId, popType, totalAdultPopulation, populationRatioOrder),
    )
}

/**
 * Increase salary if production fuel is increasing
 */
class IncreaseSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
    private val totalAdultPopulation: Double,
) : DualUtilityOption() {
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
        val maxSalary: Double = 1E10

        // Multiply this to get the new salary
        val salaryMultiplier: Double = 1.25

        val commonPopData: MutableCommonPopData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .getCommonPopData(popType)

        val physicsData: MutablePhysicsData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData()

        val currentSalary: Double = commonPopData.salaryPerEmployee

        // Bound the salary by production fuel
        val maxFuelAsSalaryPerEmployee: Double = if (totalAdultPopulation > 0.0) {
            // Only use 0.1 of the production fuel as salary
            0.1 * physicsData.fuelRestMassData.production / totalAdultPopulation
        } else {
            1.0
        }

        val newSalary: Double = listOf(
            maxSalary,
            currentSalary * salaryMultiplier,
            max(maxFuelAsSalaryPerEmployee, currentSalary),
        ).minOf { it }

        planDataAtPlayer.addCommand(
            ChangeSalaryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                carrierId = carrierId,
                popType = popType,
                salary = newSalary,
            )
        )
    }
}


/**
 * Decrease salary if production fuel is decreasing
 */
class DecreaseSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
) : DualUtilityOption() {
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
            PopulationSavingHighCompareToProduction(
                carrierId = carrierId,
                popType = popType,
                productionFuelRatio = 0.5,
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
        val minSalary: Double = 1E-10

        // Multiply this to get the new salary
        val salaryMultiplier: Double = 0.8

        val currentSalary: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .getCommonPopData(popType).salaryPerEmployee

        val newSalary: Double = max(minSalary, currentSalary * salaryMultiplier)

        planDataAtPlayer.addCommand(
            ChangeSalaryCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                carrierId = carrierId,
                popType = popType,
                salary = newSalary,
            )
        )
    }
}

/**
 * Increase the salary to a good value: higher than the desire if production fuel is sufficient
 */
class GoodSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
    private val totalAdultPopulation: Double,
    private val populationRatioOrder: List<Int>,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        PlainDualUtilityConsideration(1, 1.0, 0.1)
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val commonPopData: MutableCommonPopData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .getCommonPopData(popType)

        // Bound the salary
        val currentSalary: Double = commonPopData.salaryPerEmployee
        val minSalary: Double = currentSalary
        val maxSalary: Double = 1E10

        val resourceData: MutableResourceData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.economyData().resourceData

        val physicsData: MutablePhysicsData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.physicsData()

        // Compute total fuel needed to buy all the desire resource
        val desireResourceFuelNeeded: Double = commonPopData.desireResourceMap.keys.fold(
            0.0
        ) { acc, resourceType ->
            val desireData: MutableResourceDesireData = commonPopData.desireResourceMap.getValue(
                resourceType
            )
            val qualityClass: ResourceQualityClass = resourceData.tradeQualityClass(
                resourceType = resourceType,
                amount = desireData.desireAmount,
                targetQuality = desireData.desireQuality,
                budget = commonPopData.saving,
                preferHighQualityClass = true,
            )
            val price: Double = resourceData.getResourcePrice(resourceType, qualityClass)
            acc + price * desireData.desireAmount
        }

        // Bound the salary by production fuel
        val maxFuelAsSalary: Double = if (totalAdultPopulation > 0.0) {
            // Only use 0.1 of the production fuel as salary
            0.1 * physicsData.fuelRestMassData.production * commonPopData.adultPopulation /
                    totalAdultPopulation
        } else {
            1.0
        }

        // Compute an extra factor determined by the order of population ratio to enhance migration
        val populationOrderRatioFactor: Double = 1.0 + if (populationRatioOrder.isNotEmpty()) {
            0.05 + 0.05 * populationRatioOrder.indexOf(carrierId) / populationRatioOrder.size
        } else {
            0.1
        }

        if (commonPopData.adultPopulation > 0.0) {
            // Multiply by 1.1 so the pop can save their salary
            val salaryPerAdultPopulation: Double = min(
                desireResourceFuelNeeded,
                maxFuelAsSalary
            ) / commonPopData.adultPopulation

            val salary: Double = when {
                salaryPerAdultPopulation > maxSalary -> maxSalary
                salaryPerAdultPopulation < minSalary -> minSalary
                else -> salaryPerAdultPopulation
            } * populationOrderRatioFactor

            planDataAtPlayer.addCommand(
                ChangeSalaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    carrierId = carrierId,
                    popType = popType,
                    salary = salary,
                )
            )
        }
    }
}