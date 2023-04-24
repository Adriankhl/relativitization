package relativitization.universe.data.events

import org.junit.jupiter.api.Test

internal class DefaultEventTest {
    @Test
    fun nameOverriddenTest() {
        // All name methods should have been overridden instead of being empty
        DefaultEvent::class.sealedSubclasses.forEach {
            assert(it.java.getMethod("name").declaringClass == it.java)
        }
    }
}