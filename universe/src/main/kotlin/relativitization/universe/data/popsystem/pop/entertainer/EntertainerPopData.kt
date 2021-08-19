package relativitization.universe.data.popsystem.pop.entertainer

import kotlinx.serialization.Serializable
import relativitization.universe.data.popsystem.pop.CommonPopData
import relativitization.universe.data.popsystem.pop.MutableCommonPopData

@Serializable
data class EntertainerPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableEntertainerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)