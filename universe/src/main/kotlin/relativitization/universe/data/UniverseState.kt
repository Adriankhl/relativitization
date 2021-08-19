package relativitization.universe.data

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable

@Serializable
data class UniverseState(
    private var currentTime: Int,
    private var maxPlayerId: Int = 0,
) {
    fun getCurrentTime(): Int = currentTime

    fun updateTime() = currentTime ++

    fun getCurrentMaxId(): Int = maxPlayerId

    suspend fun getNewPlayerId(): Int {
        GetIdMutex.mutex.withLock {
            maxPlayerId++
            return maxPlayerId
        }
    }
}

/**
 * Adding mutex in the universeState data class throws serialization error, so the mutex is stored in this object
 */
object GetIdMutex {
    val mutex: Mutex = Mutex()
}