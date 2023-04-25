package relativitization.universe.game.maths.collection

import relativitization.universe.core.maths.collection.ListFind.minMissing
import kotlin.test.Test

internal class ListFindTest {
    @Test
    fun minMissingTest() {
        val l1 = listOf(1, 2, 2, 3, 5)
        assert(minMissing(l1, 1) == 4)
        assert(minMissing(l1, 0) == 0)

        val l2: List<Int> = listOf()
        assert(minMissing(l2, 2) == 2)
    }
}