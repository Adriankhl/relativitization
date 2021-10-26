package relativitization.universe.data.component.popsystem.pop.service

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.CommonPopData
import relativitization.universe.data.component.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.component.popsystem.pop.service.export.ExportData
import relativitization.universe.data.component.popsystem.pop.service.export.MutableExportData

@Serializable
data class ServicePopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val exportData: ExportData = ExportData(),
    val maxEmployee: Double = 100.0,
)

@Serializable
data class MutableServicePopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var exportData: MutableExportData = MutableExportData(),
    var maxEmployee: Double = 100.0,
)