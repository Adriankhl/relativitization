package relativitization.universe.data.events

import kotlin.test.Test

internal class EventCollectionTest {
    @Test
    fun repeatedNameTest() {
        assert(EventCollection.defaultEventList.groupingBy {
            it
        }.eachCount().any { it.value == 1 })
    }
}