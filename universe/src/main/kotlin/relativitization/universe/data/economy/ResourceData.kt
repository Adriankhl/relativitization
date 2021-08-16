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

/**
 * For aggregating resources of player into several classes
 */
enum class ResourceQualityClass {
    FIRST,
    SECOND,
    THIRD,
}

@Serializable
data class ResourceData(
    val resourceQualityMap: Map<ResourceType, Map<ResourceQualityClass, ResourceQualityData>> = mapOf(),
    val resourceAmountMap: Map<ResourceType, Map<ResourceQualityClass, Double>> = mapOf(),
    val resourcePriceMap: Map<ResourceType, Map<ResourceQualityClass, Double>> = mapOf(),
) {
    /**
     * Get resource quality, default to ResourceQualityData() if the resource doesn't exist
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceQualityData {
        return resourceQualityMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: ResourceQualityData()
    }


    /**
     * Get resource amount, default to 0.0 if the resource doesn't exist
     */
    fun getResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: 0.0
    }

    /**
     * Get resource price, default to Double.MAX_VALUE if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: Double.MAX_VALUE
    }
}

@Serializable
data class MutableResourceData(
    var resourceQualityMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceQualityData>> = mutableMapOf(),
    var resourceAmountMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
    var resourcePriceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
) {
    /**
     * Get resource quality, default to ResourceQualityData() if the resource doesn't exist
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData {
        return resourceQualityMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: MutableResourceQualityData()
    }


    /**
     * Get resource amount, default to 0.0 if the resource doesn't exist
     */
    fun getResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: 0.0
    }

    /**
     * Get resource price, default to Double.MAX_VALUE if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.get(
            resourceType
        )?.get(
            resourceQualityClass
        ) ?: Double.MAX_VALUE
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