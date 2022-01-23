package playground

import kotlin.system.measureTimeMillis
import kotlin.test.Ignore
import kotlin.test.Test

data class TestData(val name1: Int = 1) {
    fun name2(): String = "name$name1"
}

internal class ReflectionTest {
    @Ignore
    fun benchmark() {
        val repeatTimes: Int = 100000000
        val s1: Long = measureTimeMillis {
            for (i in (1..repeatTimes)) {
                val t = TestData(i)
                t.name1
            }
        }

        val s2: Long = measureTimeMillis {
            for (i in (1..repeatTimes)) {
                val t = TestData(i)
                t.name2()
            }
        }

        val s3: Long = measureTimeMillis {
            for (i in (1..repeatTimes)) {
                val t = TestData(i)
                t::class.simpleName
            }
        }

        println("Variable: $s1 ms")
        println("Function: $s2 ms")
        println("Reflection: $s3 ms")
    }
}