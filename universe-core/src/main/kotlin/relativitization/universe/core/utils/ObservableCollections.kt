package relativitization.universe.core.utils

class ObservableList<T>(
    private val mutableList: MutableList<T>,
    private val onChange: () -> Unit = {}
) {

    operator fun get(index: Int): T {
        return mutableList[index]
    }

    fun clear() {
        mutableList.clear()
        onChange()
    }

    fun add(element: T) {
        mutableList.add(element)
        onChange()
    }

    fun remove(element: T) {
        mutableList.remove(element)
        onChange()
    }

    fun getList(): List<T> {
        return mutableList
    }

    fun indexOf(element: T): Int {
        return mutableList.indexOf(element)
    }

    fun isEmpty(): Boolean {
        return mutableList.isEmpty()
    }

    fun isNotEmpty(): Boolean {
        return mutableList.isNotEmpty()
    }

    fun size(): Int {
        return mutableList.size
    }

    fun last(): T {
        return mutableList.last()
    }

    fun contains(element: T): Boolean {
        return mutableList.contains(element)
    }
}