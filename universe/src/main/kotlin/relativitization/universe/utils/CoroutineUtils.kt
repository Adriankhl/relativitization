package relativitization.universe.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CoroutineMap<K, V> {
    private val mutex: Mutex = Mutex()
    private val mutableMap: MutableMap<K, V> = mutableMapOf()

    /**
     * Append value to map, add count
     */
    suspend fun set(key: K, value: V) {
        mutex.withLock {
            mutableMap[key] = value
        }
    }

    suspend fun size(): Int {
        mutex.withLock {
            return mutableMap.size
        }
    }

    suspend fun toMap(): Map<K, V> {
        mutex.withLock {
            return mutableMap.toMap()
        }
    }

    suspend fun reset() {
        mutex.withLock {
            mutableMap.clear()
        }
    }
}

class CoroutineList<T> {
    private val mutex: Mutex = Mutex()
    private val mutableList: MutableList<T> = mutableListOf()
    private var setCount: Int = 0

    /**
     * Append value to map, add count
     */
    suspend fun addAll(list: List<T>) {
        mutex.withLock {
            mutableList.addAll(list)
            setCount++
        }
    }

    suspend fun getCount(): Int {
        mutex.withLock {
            return setCount
        }
    }

    suspend fun toList(): List<T> {
        mutex.withLock {
            return mutableList.toList()
        }
    }

    suspend fun reset() {
        mutex.withLock {
            mutableList.clear()
            setCount = 0
        }
    }
}

class CoroutineBoolean(initBool: Boolean = false) {
    private val mutex: Mutex = Mutex()
    private var bool: Boolean = initBool

    /**
     * Append value to map, add count
     */
    suspend fun set(boolean: Boolean) {
        mutex.withLock {
            bool = boolean
        }
    }

    /**
     * Check if this variable is true
     * Normally used in a loop to check the status
     */
    suspend fun isTrue(): Boolean {
        // Prevent too many call to this method, which may prevent other method to run
        delay(1)
        mutex.withLock {
            return bool
        }
    }
}

class CoroutineCounter(start: Int = -1) {
    private val mutex: Mutex = Mutex()
    private var counter: Int = start

    /**
     * get counter
     */
    suspend fun get(): Int {
        mutex.withLock {
            return ++counter
        }
    }
}

class CoroutineVar<T>(init: T) {
    private val mutex: Mutex = Mutex()
    private var t: T = init

    /**
     * Append value to map, add count
     */
    suspend fun set(store: T) {
        mutex.withLock {
            t = store
        }
    }

    /**
     * Check if this variable is true
     * Normally used in a loop to check the status
     */
    suspend fun get(): T {
        // Prevent too many call to this method, which may prevent other method to run
        delay(1)
        mutex.withLock {
            return t
        }
    }
}


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