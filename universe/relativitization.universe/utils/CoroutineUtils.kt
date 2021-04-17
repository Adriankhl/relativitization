package relativitization.universe.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/**
 * parallel map collection by coroutine
 */
suspend fun <A, B> Collection<A>.pmap(f: suspend (A) -> B): List<B> = coroutineScope {
    withContext(Dispatchers.Default) {
        /*
        val threads = Thread.getAllStackTraces().keys.filter {
            it.name.startsWith("CommonPool") || it.name.startsWith("ForkJoinPool")
        }
        println("Number of thread in coroutine: " + threads.size )
        */
        map { async { f(it) } }.awaitAll()
    }
}