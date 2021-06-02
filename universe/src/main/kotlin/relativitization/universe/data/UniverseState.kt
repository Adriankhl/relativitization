package relativitization.universe.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
data class UniverseState(
    private var currentTime: Int,
    private var maxPlayerId: Int = 0,
) {
    private val mutex: Mutex = Mutex()

    fun getCurrentTime(): Int = currentTime

    fun updateTime() = currentTime ++

    fun getCurrentMaxId(): Int = maxPlayerId

    suspend fun getNewId(): Int {
        mutex.withLock {
            maxPlayerId++
            return maxPlayerId
        }
    }
}