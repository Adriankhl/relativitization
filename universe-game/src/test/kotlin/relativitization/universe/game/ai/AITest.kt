package relativitization.universe.game.ai

import org.junit.jupiter.api.Test
import relativitization.universe.core.ai.EmptyAI

internal class AITest {
    @Test
    fun nameTest() {
        assert(EmptyAI.name() == "EmptyAI")
    }
}