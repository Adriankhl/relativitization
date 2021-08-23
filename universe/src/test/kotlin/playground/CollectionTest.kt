package playground

import kotlin.test.Test

internal class CollectionTest {
    @Test
    fun listLast() {
        val l: List<Any> = listOf(1, 2)
        val d: Double = (l.lastOrNull {
            it is Double
        } ?: run {
            println("Hello")
            3.2
        }) as Double
        println(d)
    }
}