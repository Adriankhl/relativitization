package relativitization.universe.game.data.commands

import kotlin.test.Test

internal class CommandCollectionTest {
    @Test
    fun repeatedNameTest() {
        assert(DefaultCommandAvailability.commandList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })

        assert(DefaultCommandAvailability.addEventList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })
    }
}