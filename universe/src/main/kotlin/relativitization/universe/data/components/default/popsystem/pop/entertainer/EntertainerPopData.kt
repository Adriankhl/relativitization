package relativitization.universe.data.components.default.popsystem.pop.entertainer

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.popsystem.pop.CommonPopData
import relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData

@Serializable
data class EntertainerPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData()
)

@Serializable
data class MutableEntertainerPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData()
)