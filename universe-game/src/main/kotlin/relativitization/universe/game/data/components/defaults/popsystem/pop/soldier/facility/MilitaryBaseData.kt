package relativitization.universe.game.data.components.defaults.popsystem.pop.soldier.facility

import kotlinx.serialization.Serializable
import ksergen.annotations.GenerateImmutable

@GenerateImmutable
data class MutableMilitaryBaseData(
    var attack: Double = 0.0,
    var shield: Double = 1.0,
    var lastNumEmployee: Double = 0.0,
)