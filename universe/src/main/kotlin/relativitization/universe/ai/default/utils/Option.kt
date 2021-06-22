package relativitization.universe.ai.default.utils

import relativitization.universe.data.commands.Command

interface Option {
    val considerationList: List<Consideration>

    fun getRank(): Int {
        return if (considerationList.isEmpty()) {
            0
        } else {
            considerationList.maxOf {
                it.getDualUtilityData().rank
            }
        }
    }

    fun getWeight(): Double {
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

    fun getCommandList(): List<Command>
}