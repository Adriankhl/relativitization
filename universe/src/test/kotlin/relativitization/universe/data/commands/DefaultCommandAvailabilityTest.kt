package relativitization.universe.data.commands

import kotlin.test.Test


internal class DefaultCommandAvailabilityTest {
    @Test
    fun nameOverriddenTest() {
        DefaultCommandAvailability.commandList.forEach {
            // All commands name() method should be overridden here
            assert(it.java.getMethod("name").declaringClass == it.java)
        }
    }
}