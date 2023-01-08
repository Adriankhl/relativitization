package relativitization.universe.generate.method.testing

import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class TestingFixedMinimalGenerateTest {
    @Test
    fun nameTest() {
        assert(TestingFixedMinimalGenerate.name() == "Test - fixed minimal")

        assert(GenerateUniverseMethodCollection.generateMethodMap.containsKey(TestingFixedMinimalGenerate.name()))
    }
}