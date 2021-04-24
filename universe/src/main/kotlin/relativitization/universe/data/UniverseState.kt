package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseState(
    private var currentTime: Int,
    private var maxPlayerId: Int = 0,
) {
    fun getCurrentTime(): Int = currentTime
    fun getNewId(): Int {
        maxPlayerId ++
        return maxPlayerId
    }
}