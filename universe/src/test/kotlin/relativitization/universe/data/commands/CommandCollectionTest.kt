package relativitization.universe.data.commands

import kotlin.test.Test

internal class CommandCollectionTest {
    @Test
    fun repeatedNameTest() {
        assert(DefaultCommandList.commandList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })

        assert(DefaultCommandList.eventList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })
    }
}