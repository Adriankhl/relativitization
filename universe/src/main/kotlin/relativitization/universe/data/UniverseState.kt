package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseState(
    var latestTurn: Int,
    var numTurnStore: Int,
)