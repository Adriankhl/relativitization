package relativitization.universe.utils

import kotlin.test.Test

internal class I18NStringTest {
    @Test
    fun normalStringTest() {
        val i1 = I18NString(
            listOf(
                RealString("Hello"),
                IntString(0)
            ),
            listOf(" world")
        )

        assert(i1.toNormalString() == listOf("Hello world"))

        val i2 = I18NString(
            listOf(
                RealString("Hello"),
                IntString(1)
            ),
            listOf(" world")
        )

        assert(i2.toNormalString() == listOf("Hello"))
    }

    @Test
    fun messageFormatTest() {
        val i1 = I18NString(
            listOf(
                RealString("Hello "),
                IntString(0)
            ),
            listOf("world")
        )

        println(i1.toMessageFormat() == listOf(listOf("[Hello {0}", "world")))
    }
}