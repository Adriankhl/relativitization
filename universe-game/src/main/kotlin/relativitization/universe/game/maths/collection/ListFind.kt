package relativitization.universe.game.maths.collection

object ListFind {
    /**
     * Return an integer greater than or equal to lowerbound that is not in the list
     */
    fun minMissing(list: List<Int>, lowerBound: Int): Int {
        val sortedList: List<Int> = list.sorted()

        return sortedList.fold(lowerBound) { acc, t ->
            if (acc == t) {
                acc + 1
            } else {
                acc
            }
        }
    }
}