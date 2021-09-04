package relativitization.universe.data.commands

import kotlin.test.Test

internal class CommandCollectionTest {
    @Test
    fun repeatedNameTest() {
        assert(CommandCollection.defaultCommandList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })
    }
}