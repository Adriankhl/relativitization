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

    fun updateTime() = currentTime++

    fun getCurrentMaxId(): Int = maxPlayerId

    suspend fun getNewPlayerId(): Int {
        mutex.withLock {
            maxPlayerId++
            return maxPlayerId
        }
    }

    companion object {
        // Adding mutex in the universeState data class throws serialization error,
        // so the mutex is stored in this object
        private val mutex: Mutex = Mutex()
    }
}