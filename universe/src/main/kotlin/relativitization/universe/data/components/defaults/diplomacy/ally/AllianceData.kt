package relativitization.universe.data.components.defaults.diplomacy.ally

import kotlinx.serialization.Serializable

@Serializable
data class AllianceData(
    val startTime: Int = 0,
)

@Serializable
data class MutableAllianceData(
    var startTime: Int = 0,
)