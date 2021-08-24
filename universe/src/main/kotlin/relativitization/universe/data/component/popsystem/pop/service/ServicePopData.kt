package relativitization.universe.data.component.popsystem.pop.service

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.popsystem.pop.CommonPopData
import relativitization.universe.data.component.popsystem.pop.MutableCommonPopData

@Serializable
data class ServicePopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableServicePopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)