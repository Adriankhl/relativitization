package relativitization.utils

import java.net.Socket

object ServerPort {
    fun findAvailablePort(initPort: Int = 29979): Int {
        val available: Boolean = try {
            Socket("127.0.0.1", initPort)
            false
        } catch (e: Throwable) {
            true
        }

        return if (available) {
            initPort
        } else {
            findAvailablePort(initPort + 1)
        }
    }
}