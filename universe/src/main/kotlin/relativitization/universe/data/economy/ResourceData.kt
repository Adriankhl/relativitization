package relativitization.universe.data.economy

import kotlinx.serialization.Serializable

enum class ResourceType(val value: String) {
    PLANT("Plant"), // Raw material
    ANIMAL("Animal"),
    METAL("Metal"),
    ELECTRONIC("Electrical equipment"), // Intermediate material
    MACHINE_ELEMENT("Machine element"),
    CONSTRUCTION_MATERIAL("Construction material"),
    OPTICAL_ELEMENT("Optical element"),
    FOOD("Food"), // Final product
    CLOTH("Cloth"),
    HOUSEHOLD_GOOD("Household good"),
    ENTERTAINMENT("Entertainment"),
    SOFTWARE("Software"),
    RESEARCH_EQUIPMENT("Research equipment"),
    MEDICINE("Medicine"),
    TRANSPORTATION("Transportation"),
    AMMUNITION("Ammunition"),
    LASER("Laser"),
    ROBOT("Robot"),
    ;

    override fun toString(): String {
        return value
    }
}

@Serializable
data class ResourceQualityData(
    val quality1: Double = 0.0,
    val quality2: Double = 0.0,
    val quality3: Double = 0.0,
)

@Serializable
data class MutableResourceQualityData(
    var quality1: Double = 0.0,
    var quality2: Double = 0.0,
    var quality3: Double = 0.0,
)

@Serializable
data class ResourceStockData(
    val firstClassQuality: ResourceQualityData = ResourceQualityData(),
    val firstClassAmount: Double = 0.0,
    val secondClassQuality: ResourceQualityData = ResourceQualityData(),
    val secondClassAmount: Double = 0.0,
    val thirdClassQuality: ResourceQualityData = ResourceQualityData(),
    val thirdClassAmount: Double = 0.0,
)

@Serializable
data class MutableResourceStockData(
    var firstClassQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var firstClassAmount: Double = 0.0,
    var secondClassQuality: MutableResourceQualityData = MutableResourceQualityData(), var secondClassAmount: Double = 0.0,
    var thirdClassQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var thirdClassAmount: Double = 0.0,
)


@Serializable
data class ResourceMarketData(
    val allClassResourceStockData: ResourceStockData = ResourceStockData(),
    val firstClassFuelRestMassPrice: Double = 1.0,
    val secondClassFuelRestMassPrice: Double = 1.0,
    val thirdClassFuelRestMassPrice: Double = 1.0,
)

@Serializable
data class MutableResourceMarketData(
    var allClassResourceStockData: MutableResourceStockData = MutableResourceStockData(),
    var firstClassFuelRestMassPrice: Double = 1.0,
    var secondClassFuelRestMassPrice: Double = 1.0,
    var thirdClassFuelRestMassPrice: Double = 1.0,
)
