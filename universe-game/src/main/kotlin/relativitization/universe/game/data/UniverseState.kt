package relativitization.universe.game.data

import kotlinx.coroutines.runBlocking
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

    fun getNewPlayerId(): Int {
        return runBlocking {
            mutex.withLock {
                maxPlayerId++
                maxPlayerId
            }
        }
    }

    companion object {
        // Adding mutex in the universeState data class throws serialization error,
        // so the mutex is stored in this object
        private val mutex: Mutex = Mutex()
    }
}