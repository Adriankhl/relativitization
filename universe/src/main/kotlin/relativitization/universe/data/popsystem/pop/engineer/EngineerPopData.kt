package relativitization.universe.data.popsystem.pop.engineer

import kotlinx.serialization.Serializable
import relativitization.universe.data.popsystem.pop.CommonPopData
import relativitization.universe.data.popsystem.pop.MutableCommonPopData

@Serializable
data class EngineerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEngineerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)
