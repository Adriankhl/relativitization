package relativitization.universe.data.subsystem.popsystem.pop.scholar

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.pop.CommonPopData
import relativitization.universe.data.subsystem.popsystem.pop.MutableCommonPopData

@Serializable
data class ScholarPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableScholarPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)