package relativitization.universe.data

import kotlinx.serialization.Serializable

@Serializable
data class UniverseState(
    private var currentTime: Int,
) {
    fun getCurrentTime(): Int = currentTime
}