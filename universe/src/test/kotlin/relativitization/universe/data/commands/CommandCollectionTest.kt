package relativitization.universe.data.commands

import kotlin.test.Test

internal class CommandCollectionTest {
    @Test
    fun repeatedNameTest() {
        assert(DefaultAvailableCommands.commandList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })

        assert(DefaultAvailableCommands.addEventList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })
    }
}