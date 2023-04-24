package relativitization.universe.data.components.defaults.popsystem.pop.medic

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData

@Serializable
data class MedicPopData(
    val commonPopData: CommonPopData = CommonPopData()
)

@Serializable
data class MutableMedicPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)