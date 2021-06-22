package relativitization.universe.ai.default.utils

import relativitization.universe.data.commands.Command

interface Option {
    val considerationList: List<Consideration>

    fun getDualUtilityData(): DualUtilityData {

        return considerationList.fold(DualUtilityData()) { acc, consideration ->
            acc.combine(consideration.getDualUtilityData())
        }
    }

    fun getCommandList(): List<Command>
}