package relativitization.universe.data.component.popsystem.pop.service.export

import kotlinx.serialization.Serializable

@Serializable
data class ExportData(
    val playerExportCenterMap: Map<Int, PlayerExportCenterData> = mapOf(),
    val popExportCenterMap: Map<Int, PopExportCenterData> = mapOf(),
)

@Serializable
data class MutableExportData(
    val playerExportCenterMap: MutableMap<Int, MutablePlayerExportCenterData> = mutableMapOf(),
    val popExportCenterMap: MutableMap<Int, MutablePopExportCenterData> = mutableMapOf(),
)