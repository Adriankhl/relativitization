package relativitization.universe

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Settings of server
 */
data class UniverseServerSettings(
    private var adminPassword: String,
    private var clearInactivePerTurn: Boolean = false,
    private var waitTimeLimit: Int = 60,
) {
    private val mutex: Mutex = Mutex()

    suspend fun getAdminPassword(): String {
        mutex.withLock {
            return adminPassword
        }
    }

    suspend fun getCleanInactivePerTurn(): Boolean {
        mutex.withLock {
            return clearInactivePerTurn
        }
    }

    suspend fun getWaitTimeLimit(): Int {
        mutex.withLock {
            return waitTimeLimit
        }
    }
}