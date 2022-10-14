package relativitization.universe.ai

import org.junit.jupiter.api.Test

internal class AITest {
    @Test
    fun nameTest() {
        assert(EmptyAI.name() == "EmptyAI")
    }
}