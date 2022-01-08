package relativitization.universe.ai.defaults.node.self.pop

import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.ChangeSalaryCommand
import relativitization.universe.data.components.MutablePhysicsData
import relativitization.universe.data.components.defaults.economy.MutableResourceData
import relativitization.universe.data.components.defaults.economy.ResourceQualityClass
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableResourceDesireData
import relativitization.universe.data.components.defaults.popsystem.pop.PopType
import kotlin.math.min

class SalaryReasoner : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {

       val totalAdultPopulation: Double = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.popSystemData().totalAdultPopulation()

        return planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
            .popSystemData().carrierDataMap.keys.map { carrierId ->
                PopType.values().map { popType ->
                    AdjustSalaryReasoner(
                        carrierId,
                        popType,
                        totalAdultPopulation,
                    )
                }
            }.flatten()
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
        IncreaseSalaryOption(carrierId, popType),
        DecreaseSalaryOption(carrierId, popType),
        GoodSalaryOption(carrierId, popType, totalAdultPopulation),
    )
}

class IncreaseSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf()
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
    }
}

class DecreaseSalaryOption(
    private val carrierId: Int,
    private val popType: PopType,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf()
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
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
        PlainDualUtilityConsideration(1, 1.0, 1.0)
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

        // Max fuel as total salary
        val maxFuel: Double = if (totalAdultPopulation > 0.0) {
            // Only use 0.1 of the production fuel as salary
            0.1 * physicsData.fuelRestMassData.production * commonPopData.adultPopulation /
                    totalAdultPopulation
        } else {
            1.0
        }

        if (commonPopData.adultPopulation > 0.0) {
            // Multiply by 1.1 so the pop can save their salary
            val salaryPerAdultPopulation: Double = min(desireResourceFuelNeeded * 1.1, maxFuel) /
                    commonPopData.adultPopulation

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