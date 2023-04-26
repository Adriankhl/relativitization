package relativitization.universe.game.data.components.defaults.popsystem.pop.service

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.ExportData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutableExportData

@GenerateImmutable
data class MutableServicePopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var exportData: MutableExportData = MutableExportData(),
)