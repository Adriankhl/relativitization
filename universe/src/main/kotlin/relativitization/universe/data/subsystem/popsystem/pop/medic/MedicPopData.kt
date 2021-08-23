package relativitization.universe.data.subsystem.popsystem.pop.medic

import kotlinx.serialization.Serializable
import relativitization.universe.data.subsystem.popsystem.pop.CommonPopData
import relativitization.universe.data.subsystem.popsystem.pop.MutableCommonPopData

@Serializable
data class MedicPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableMedicPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)