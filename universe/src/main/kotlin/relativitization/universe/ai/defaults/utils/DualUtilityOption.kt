package relativitization.universe.ai.defaults.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.random.Random

abstract class DualUtilityOption(random: Random) : AINode(random) {
    abstract fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): List<DualUtilityConsideration>

    fun getRank(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): Int {
        val considerationList: List<DualUtilityConsideration> = getConsiderationList(planDataAtPlayer, planState)
        return if (considerationList.isEmpty()) {
            0
        } else {
            considerationList.maxOf {
                it.getDualUtilityData(planDataAtPlayer, planState).rank
            }
        }
    }

    fun getWeight(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): Double {
        val considerationList: List<DualUtilityConsideration> = getConsiderationList(planDataAtPlayer, planState)
        return if (considerationList.isEmpty()) {
            0.0
        } else {
            val utilityDataList: List<DualUtilityData> = considerationList.map {
                it.getDualUtilityData(planDataAtPlayer, planState)
            }

            val totalBonus: Double = utilityDataList.fold(0.0) { acc, data ->
                acc + data.bonus
            }
            val totalMultiplier: Double = utilityDataList.fold(1.0) { acc, data ->
                acc * data.multiplier
            }

            totalMultiplier * totalBonus
        }
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}

class EmptyDualUtilityOption(random: Random) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf()

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) { }
}

class DoNothingDualUtilityOption(
    val rank: Int,
    val multiplier: Double,
    val bonus: Double,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        PlainDualUtilityConsideration(
            rank = rank,
            multiplier = multiplier,
            bonus = bonus
        )
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) { }
}

