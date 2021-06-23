package relativitization.universe.ai.default.utils

import relativitization.universe.data.commands.Command

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

abstract class CommandListOption(private val decisionData: DecisionData) : Option {
    protected abstract fun getCommandList(): List<Command>

    // Extra step to update Decision Data beside adding command
    protected abstract fun extraUpdateDecisionData()

    override fun updateData() {
        val commandList = getCommandList()
        decisionData.addCommands(commandList)
        extraUpdateDecisionData()
    }
}