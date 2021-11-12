package relativitization.universe.data.components.popsystem.pop.soldier.facility

import kotlinx.serialization.Serializable

@Serializable
data class MilitaryBaseData(
    val attack: Double = 0.0,
    val shield: Double = 1.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
)

@Serializable
data class MutableMilitaryBaseData(
    val attack: Double = 0.0,
    val shield: Double = 1.0,
    val maxNumEmployee: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
)