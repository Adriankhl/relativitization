package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

abstract class Option : AINode {
    abstract fun getConsiderationList(): List<Consideration>

    protected abstract fun getCommandList(): List<Command>

    fun getRank(): Int {
        val considerationList = getConsiderationList()
        return if (considerationList.isEmpty()) {
            0
        } else {
            considerationList.maxOf {
                it.getDualUtilityData().rank
            }
        }
    }

    fun getWeight(): Double {
        val considerationList = getConsiderationList()
        return if (considerationList.isEmpty()) {
            0.0
        } else {
            val utilityDataList: List<DualUtilityData> = considerationList.map {
                it.getDualUtilityData()
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


    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planStatus: PlanStatus) {
        logger.debug("${this::class.simpleName} (CommandListOption) updating data")

        val commandList = getCommandList()

        planDataAtPlayer.addAllCommand(commandList)
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}