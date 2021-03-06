package relativitization.universe.data.components.defaults.popsystem.pop.educator

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData

@Serializable
data class EducatorPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEducatorPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)