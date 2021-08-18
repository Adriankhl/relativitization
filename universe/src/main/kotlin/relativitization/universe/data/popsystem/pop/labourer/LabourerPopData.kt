package relativitization.universe.data.popsystem.pop.labourer

import kotlinx.serialization.Serializable
import relativitization.universe.data.popsystem.pop.CommonPopData
import relativitization.universe.data.popsystem.pop.MutableCommonPopData

@Serializable
data class LabourerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableLabourerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)