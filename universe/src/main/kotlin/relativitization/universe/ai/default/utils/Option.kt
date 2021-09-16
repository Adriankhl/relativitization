package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

abstract class Option : AINode {
    abstract fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): List<Consideration>

    protected abstract fun getCommandList(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): List<Command>

    protected open fun updateStatus(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ) {}

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        logger.debug("${this::class.simpleName} (CommandListOption) updating data")

        val commandList = getCommandList(planDataAtPlayer, planState)

        planDataAtPlayer.addAllCommand(commandList)

        updateStatus(planDataAtPlayer, planState)
    }

    fun getRank(
        planDataAtPlayer: PlanDataAtPlayer, planState: PlanState
    ): Int {
        val considerationList = getConsiderationList(planDataAtPlayer, planState)
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
        val considerationList = getConsiderationList(planDataAtPlayer, planState)
        return if (considerationList.isEmpty()) {
            0.0
        } else {
            val utilityDataList: List<DualUtilityData> = considerationList.map {
                it.getDualUtilityData(planDataAtPlayer, planState)
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

class EmptyOption : Option() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Consideration> = listOf()

    override fun getCommandList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<Command>  = listOf()
}