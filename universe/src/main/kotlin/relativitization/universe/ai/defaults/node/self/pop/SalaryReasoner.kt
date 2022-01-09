package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.consideration.fuel.IncreasingProductionFuelConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeSalaryCommand
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import kotlin.math.max
import kotlin.math.min

class SalaryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {

        val totalAdultPopulation: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().totalAdultPopulation()

        val adjustSalaryReasonerList: List<AdjustSalaryReasoner> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
                .popSystemData().carrierDataMap.keys.map { carrierId ->
                    PopType.values().map { popType ->
                        AdjustSalaryReasoner(
                            carrierId,
                            popType,
                            totalAdultPopulation,
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
) : DualUtilityReasoner() {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> = listOf(
        IncreaseSalaryOption(carrierId, popType, totalAdultPopulation),
        DecreaseSalaryOption(carrierId, popType),
        GoodSalaryOption(carrierId, popType, totalAdultPopulation),
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
                rankIfTrue = 5,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
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
            maxFuelAsSalaryPerEmployee,
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
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 5,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 1.0
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
 * Set the salary to a good value: higher than the desire if production fuel is sufficient
 */
class GoodSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
    private val totalAdultPopulation: Double,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        PlainDualUtilityConsideration(1, 1.0, 0.01)
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val commonPopData: MutableCommonPopData = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().carrierDataMap.getValue(carrierId).allPopData
            .getCommonPopData(popType)

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

        if (commonPopData.adultPopulation > 0.0) {
            // Multiply by 1.1 so the pop can save their salary
            val salaryPerAdultPopulation: Double = min(
                desireResourceFuelNeeded * 1.1,
                maxFuelAsSalary
            ) / commonPopData.adultPopulation

            planDataAtPlayer.addCommand(
                ChangeSalaryCommand(
                    toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    carrierId = carrierId,
                    popType = popType,
                    salary = salaryPerAdultPopulation,
                )
            )
        }
    }
}