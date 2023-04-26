package relativitization.universe.game.data.components.defaults.popsystem.pop.service.export

import ksergen.annotations.GenerateImmutable

/**
 * Data of export center
 *
 * @property playerExportCenterMap a map from owner playerId to PlayerExportCenterData
 * @property popExportCenterMap a map from owner playerId to PopExportCenterData
 */
@GenerateImmutable
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
        playerExportCenterMap.values.removeAll { exportCenter ->
            exportCenter.exportDataList.isEmpty()
        }

        // Clear empty pop export center
        playerExportCenterMap.values.removeAll { exportCenter ->
            exportCenter.exportDataList.isEmpty()
        }
    }
}