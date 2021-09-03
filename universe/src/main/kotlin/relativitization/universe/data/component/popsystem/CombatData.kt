package relativitization.universe.data.component.popsystem

import kotlinx.serialization.Serializable

@Serializable
data class CombatData(
    val strength: Double = 0.0,
    val morale: Double = 0.0,
    val attack: Double = 0.0,
)

@Serializable
data class MutableCombatData(
    var strength: Double = 0.0,
    var morale: Double = 0.0,
    var attack: Double = 0.0,
)