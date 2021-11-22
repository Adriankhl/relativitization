package relativitization.universe.data.components.default.popsystem.pop.service

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.service.export.ExportData
import relativitization.universe.data.components.default.popsystem.pop.service.export.MutableExportData

@Serializable
data class ServicePopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData(),
    val exportData: ExportData = ExportData(),
    val maxEmployee: Double = 100.0,
)

@Serializable
data class MutableServicePopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData(),
    var exportData: MutableExportData = MutableExportData(),
    var maxEmployee: Double = 100.0,
)