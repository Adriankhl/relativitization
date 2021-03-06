package relativitization.universe.data.components.defaults.popsystem.pop.soldier

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.popsystem.pop.CommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.MutableCommonPopData
import relativitization.universe.data.components.defaults.popsystem.pop.soldier.facility.MilitaryBaseData
import relativitization.universe.data.components.defaults.popsystem.pop.soldier.facility.MutableMilitaryBaseData

@Serializable
data class SoldierPopData(
    val commonPopData: CommonPopData = CommonPopData(),
    val militaryBaseData: MilitaryBaseData = MilitaryBaseData(),
)

@Serializable
data class MutableSoldierPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData(),
    var militaryBaseData: MutableMilitaryBaseData = MutableMilitaryBaseData(),
)