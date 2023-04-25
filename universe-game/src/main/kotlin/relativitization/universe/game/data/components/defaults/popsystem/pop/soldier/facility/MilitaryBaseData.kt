package relativitization.universe.game.data.components.defaults.popsystem.pop.soldier.facility

import kotlinx.serialization.Serializable

@Serializable
data class MilitaryBaseData(
    val attack: Double = 0.0,
    val shield: Double = 1.0,
    val lastNumEmployee: Double = 0.0,
)

@Serializable
data class MutableMilitaryBaseData(
    var attack: Double = 0.0,
    var shield: Double = 1.0,
    var lastNumEmployee: Double = 0.0,
)