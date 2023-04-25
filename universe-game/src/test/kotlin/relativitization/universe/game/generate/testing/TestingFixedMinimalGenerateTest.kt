package relativitization.universe.game.generate.testing

import relativitization.universe.core.generate.GenerateUniverseMethodCollection
import kotlin.test.Test

internal class TestingFixedMinimalGenerateTest {
    @Test
    fun nameTest() {
        assert(TestingFixedMinimalGenerate.name() == "Test - fixed minimal")

        assert(
            GenerateUniverseMethodCollection.getGenerateUniverseMethodNames()
                .contains(TestingFixedMinimalGenerate.name())
        )
    }
}