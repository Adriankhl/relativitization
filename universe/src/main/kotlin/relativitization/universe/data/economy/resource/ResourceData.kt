package relativitization.universe.data.economy.resource

import kotlinx.serialization.Serializable

enum class ResourceType(val value: String) {
    PLANT("Plant"),
    ANIMAL("Animal"),
    METAL("Metal"),
    FOOD("Food"),
    CLOTH("Cloth"),
    HOUSEHOLD_GOOD("Household good"),
    ENTERTAINMENT("Entertainment"),
    SOFTWARE("Software"),
    ELECTRONIC("Electrical equipment"),
    CONSTRUCTION_MATERIAL("Construction material"),
    MACHINE_ELEMENT("Machine element"),
    OPTICAL_ELEMENT("Optical element"),
    RESEARCH_EQUIPMENT("Research equipment"),
    MEDICINE("Medicine"),
    TRANSPORT("Transport"),
    AMMUNITION("Ammunition"),
    LASER("Laser"),
    ROBOT("Robot"),
    ;

    override fun toString(): String {
        return value
    }
}

@Serializable
data class ResourceData(
    val type: ResourceType = ResourceType.PLANT,
    val amount: Double = 0.0,
    val resourceProperty: ResourcePropertyData = ResourcePropertyData()
)

@Serializable
data class MutableResourceData(
    var type: ResourceType = ResourceType.PLANT,
    var amount: Double = 0.0,
    var resourceProperty: MutableResourcePropertyData = MutableResourcePropertyData()
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