package relativitization.universe.maths.collection

import relativitization.universe.maths.collection.ListFind.minMissing
import kotlin.test.Test

internal class ListFindTest {
    @Test
    fun minMissingTest() {
        val l1 = listOf(1, 2, 2, 3, 5)
        assert(minMissing(l1, 1) == 4)
        assert(minMissing(l1, 0) == 0)
    }
}