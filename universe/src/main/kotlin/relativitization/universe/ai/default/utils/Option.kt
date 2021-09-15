package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

abstract class Option : AINode {
    abstract fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ): List<Consideration>

    protected abstract fun getCommandList(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ): List<Command>

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.simpleName} (CommandListOption) updating data")

        val commandList = getCommandList(planDataAtPlayer, planStatus)

        planDataAtPlayer.addAllCommand(commandList)
    }

    fun getRank(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ): Int {
        val considerationList = getConsiderationList(planDataAtPlayer, planStatus)
        return if (considerationList.isEmpty()) {
            0
        } else {
            considerationList.maxOf {
                it.getDualUtilityData(planDataAtPlayer, planStatus).rank
            }
        }
    }

    fun getWeight(
        planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus
    ): Double {
        val considerationList = getConsiderationList(planDataAtPlayer, planStatus)
        return if (considerationList.isEmpty()) {
            0.0
        } else {
            val utilityDataList: List<DualUtilityData> = considerationList.map {
                it.getDualUtilityData(planDataAtPlayer, planStatus)
            }

            val totalAddend: Double = utilityDataList.fold(0.0) { acc, data->
                acc + data.addend
            }
            val totalMultiplier: Double = utilityDataList.fold(1.0) { acc, data->
                acc * data.multiplier
            }

            totalMultiplier * totalAddend
        }
    }


    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}