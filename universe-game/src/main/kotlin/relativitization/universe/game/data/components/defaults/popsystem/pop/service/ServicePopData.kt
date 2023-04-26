package relativitization.universe.game.data.components.defaults.popsystem.pop.service

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.game.data.components.defaults.popsystem.pop.service.export.MutableExportData

@GenerateImmutable
data class MutableServicePopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var exportData: MutableExportData = MutableExportData(),
)