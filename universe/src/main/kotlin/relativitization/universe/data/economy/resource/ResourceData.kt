package relativitization.universe.data.economy.resource

import kotlinx.serialization.Serializable

enum class ResourceType(val value: String) {
    FOOD("Food"),
    RESEARCH_EQUIPMENT("Research equipment")
    ;

    override fun toString(): String {
        return value
    }
}

@Serializable
data class ResourceData(
    val resourceProperty: ResourcePropertyData = ResourcePropertyData()
)

@Serializable
data class MutableResourceData(
    val resourceProperty: MutableResourcePropertyData = MutableResourcePropertyData()
)

@Serializable
data class ResourcePropertyData(
    val property1: Double = 0.0,
    val property2: Double = 0.0,
    val property3: Double = 0.0,
)

@Serializable
data class MutableResourcePropertyData(
    var property1: Double = 0.0,
    var property2: Double = 0.0,
    var property3: Double = 0.0,
)