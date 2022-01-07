package relativitization.universe.data.components.defaults.ai

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData

/**
 * Store the history of fuel rest mass data
 *
 * @property maxStoredTurn maximum stored fuel rest mass data
 * @property historyList store the history in a list
 */
@Serializable
data class FuelRestMassHistoryData(
    val maxStoredTurn: Int = 5,
    val historyList: List<FuelRestMassData> = listOf(),
)

@Serializable
data class MutableFuelRestMassHistoryData(
    var maxStoredTurn: Int = 5,
    var historyList: MutableList<MutableFuelRestMassData> = mutableListOf(),
)