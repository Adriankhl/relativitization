package relativitization.universe.game.data.components.defaults.popsystem.pop.entertainer

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.popsystem.pop.MutableCommonPopData

@GenerateImmutable
data class MutableEntertainerPopData(
    var commonPopData: MutableCommonPopData = MutableCommonPopData()
)