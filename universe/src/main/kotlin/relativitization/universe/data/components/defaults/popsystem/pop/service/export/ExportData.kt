package relativitization.universe.data.components.defaults.popsystem.pop.service.export

import kotlinx.serialization.Serializable

/**
 * Data of export center
 *
 * @property playerExportCenterMap a map from owner playerId to PlayerExportCenterData
 * @property popExportCenterMap a map from owner playerId to PopExportCenterData
 */
@Serializable
data class ExportData(
    val playerExportCenterMap: Map<Int, PlayerExportCenterData> = mapOf(),
    val popExportCenterMap: Map<Int, PopExportCenterData> = mapOf(),
)

@Serializable
data class MutableExportData(
    val playerExportCenterMap: MutableMap<Int, MutablePlayerExportCenterData> = mutableMapOf(),
    val popExportCenterMap: MutableMap<Int, MutablePopExportCenterData> = mutableMapOf(),
) {
    fun totalExportAmount(): Double {
        val playerExportAmount: Double = playerExportCenterMap.values.sumOf { exportCenter ->
            exportCenter.exportDataList.sumOf {
                it.amountPerTime
            }
        }

        val popExportAmount: Double = popExportCenterMap.values.sumOf { popExportCenter ->
            popExportCenter.exportDataMap.values.sumOf { popExportCenterMap ->
                popExportCenterMap.values.sumOf { popExportCenterList ->
                    popExportCenterList.sumOf {
                        it.amountPerTime
                    }
                }
            }
        }

        return playerExportAmount + popExportAmount
    }

    fun clearExportCenterData() {

        // Clear export center of player and pop
        playerExportCenterMap.values.forEach { it.clearExportData() }
        popExportCenterMap.values.forEach { it.clearExportData() }

        // Clear empty player export center
        val playerExportCenterToRemove: Set<Int> = playerExportCenterMap.filter { (_, exportCenter) ->
            exportCenter.exportDataList.isEmpty()
        }.keys

        playerExportCenterToRemove.forEach {
            playerExportCenterMap.remove(it)
        }

        // Clear empty pop export center
        val popExportCenterToRemove: Set<Int> = playerExportCenterMap.filter { (_, exportCenter) ->
            exportCenter.exportDataList.isEmpty()
        }.keys

        popExportCenterToRemove.forEach {
            popExportCenterMap.remove(it)
        }
    }
}