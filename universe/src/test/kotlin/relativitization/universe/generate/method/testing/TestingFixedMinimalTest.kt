package relativitization.universe.generate.method.testing

import relativitization.universe.generate.GenerateUniverseMethodCollection
import relativitization.universe.generate.name
import relativitization.universe.generate.testing.TestingFixedMinimal
import kotlin.test.Test

internal class TestingFixedMinimalTest {
    @Test
    fun nameTest() {
        assert(TestingFixedMinimal.name() == "TestingFixedMinimal")

        assert(GenerateUniverseMethodCollection.generateMethodMap.containsKey(TestingFixedMinimal.name()))
    }
}