package relativitization.universe.data.components.default.popsystem.pop.educator

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.CommonPopData
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData

@Serializable
data class EducatorPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEducatorPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)