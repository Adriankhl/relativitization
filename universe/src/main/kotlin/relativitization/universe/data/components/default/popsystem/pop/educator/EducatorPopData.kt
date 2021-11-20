package relativitization.universe.data.components.default.popsystem.pop.educator

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.CommonPopData
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData

@Serializable
data class EducatorPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData()
)

@Serializable
data class MutableEducatorPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData()
)