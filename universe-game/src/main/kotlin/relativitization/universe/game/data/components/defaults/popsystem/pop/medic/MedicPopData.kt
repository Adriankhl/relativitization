package relativitization.universe.game.data.components.defaults.popsystem.pop.medic

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData

@GenerateImmutable
data class MutableMedicPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)