package relativitization.universe.generate.method.testing

import relativitization.universe.generate.method.GenerateUniverseMethodCollection
import relativitization.universe.generate.method.name
import kotlin.test.Test

internal class TestingFixedMinimalTest {
    @Test
    fun nameTest() {
        assert(TestingFixedMinimal.name() == "TestingFixedMinimal")

        assert(GenerateUniverseMethodCollection.generateMethodMap.containsKey(TestingFixedMinimal.name()))
    }
}