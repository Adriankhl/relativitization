package relativitization.universe.data.components.popsystem.pop.soldier

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.popsystem.pop.CommonPopData
import relativitization.universe.data.components.popsystem.pop.MutableCommonPopData

@Serializable
data class SoldierPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableSoldierPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)