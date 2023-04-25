package relativitization.universe.game.generate.testing

import relativitization.universe.game.generate.GenerateUniverseMethodCollection
import relativitization.universe.game.generate.testing.TestingFixedMinimalGenerate
import kotlin.test.Test

internal class TestingFixedMinimalGenerateTest {
    @Test
    fun nameTest() {
        assert(TestingFixedMinimalGenerate.name() == "Test - fixed minimal")

        assert(
            GenerateUniverseMethodCollection.generateMethodMap.containsKey(
            TestingFixedMinimalGenerate.name())
        )
    }
}