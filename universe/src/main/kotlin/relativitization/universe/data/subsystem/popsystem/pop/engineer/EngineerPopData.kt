package relativitization.universe.data.subsystem.popsystem.pop.engineer

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.pop.CommonPopData
import relativitization.universe.data.subsystem.popsystem.pop.MutableCommonPopData

@Serializable
data class EngineerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEngineerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)
