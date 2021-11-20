package relativitization.universe.data.serializer

import kotlin.test.Test

abstract class TestAbstract

sealed class TestSeal : TestAbstract()

data class TestData(
    val testNum: Int = 1
) : TestSeal()

internal class DataSerializerTest {

    @Test
    fun sealSubclassTest() {

    }
}