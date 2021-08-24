package relativitization.universe.data.component.popsystem.pop.soldier

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.CommonPopData
import relativitization.universe.data.component.popsystem.pop.MutableCommonPopData

@Serializable
data class SoldierPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableSoldierPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)