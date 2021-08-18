package relativitization.universe.data.popsystem.pop.service

import kotlinx.serialization.Serializable
import relativitization.universe.data.popsystem.pop.CommonPopData
import relativitization.universe.data.popsystem.pop.MutableCommonPopData

@Serializable
data class ServicePopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableServicePopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)