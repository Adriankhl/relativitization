package relativitization.universe.ai.default.utils

import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.Command
import relativitization.universe.utils.RelativitizationLogManager

interface Option {
    fun getConsiderationList(): List<Consideration>

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

    fun updateData()
}

abstract class CommandListOption(private val planDataAtPlayer: PlanDataAtPlayer) : Option {
    protected abstract fun getCommandList(): List<Command>

    // Extra step to update Decision Data beside adding command
    protected open fun extraUpdateDecisionData() {}

    override fun updateData() {
        val className = this::class.qualifiedName

        logger.debug("$className (CommandListOption) updating data")

        val commandList = getCommandList()
        planDataAtPlayer.addAllCommand(commandList)
        extraUpdateDecisionData()
    }

    companion object {
        private val logger = RelativitizationLogManager.getLogger()
    }
}