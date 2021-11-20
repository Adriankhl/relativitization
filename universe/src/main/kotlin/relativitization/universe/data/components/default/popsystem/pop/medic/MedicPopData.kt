package relativitization.universe.data.components.default.popsystem.pop.medic

import kotlinx.serialization.Serializable

@Serializable
data class MedicPopData(
    val commonPopData: relativitization.universe.data.components.default.popsystem.pop.CommonPopData = relativitization.universe.data.components.default.popsystem.pop.CommonPopData()
)

@Serializable
data class MutableMedicPopData(
    var commonPopData: relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData = relativitization.universe.data.components.default.popsystem.pop.MutableCommonPopData()
)