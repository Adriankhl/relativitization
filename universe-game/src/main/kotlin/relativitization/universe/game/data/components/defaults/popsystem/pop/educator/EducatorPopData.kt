package relativitization.universe.game.data.components.defaults.popsystem.pop.educator

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData

@GenerateImmutable
data class MutableEducatorPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)