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

/**
 * Resource data, a resource of a player has ResourceType and ResourceQualityClass
 *
 * @property resourceQualityMap map from ResourceType and ResourceQualityClass to resource quality
 * @property resourceAmountMap map from ResourceType and ResourceQualityClass to resource amount
 * @property resourceTradeFractionMap map from ResourceType and ResourceQualityClass to the fraction
 * of resource available for trade
 * @property resourceProductionFractionMap map from ResourceType and ResourceQualityClass to the fraction
 * of resource available for production
 * @property resourcePriceMap map from ResourceType and ResourceQualityClass to resource price in
 * fuel rest mass
 */
@Serializable
data class ResourceData(
    val resourceQualityMap: Map<ResourceType, Map<ResourceQualityClass, ResourceQualityData>> = mapOf(),
    val resourceAmountMap: Map<ResourceType, Map<ResourceQualityClass, Double>> = mapOf(),
    val resourceTradeFractionMap: Map<ResourceType, Map<ResourceQualityClass, Double>> = mapOf(),
    val resourceProductionFractionMap: Map<ResourceType, Map<ResourceQualityClass, Double>> = mapOf(),
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
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
    }


    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        val amount: Double = getResourceAmount(resourceType, resourceQualityClass)
        val fraction: Double = resourceTradeFractionMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
        return amount * fraction
    }

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        val amount: Double = getResourceAmount(resourceType, resourceQualityClass)
        val fraction: Double = resourceProductionFractionMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
        return amount * fraction
    }


    /**
     * Get resource price, default to Double.MAX_VALUE if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        ) ?: Double.MAX_VALUE
    }
}

@Serializable
data class MutableResourceData(
    var resourceQualityMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceQualityData>> = mutableMapOf(),
    var resourceAmountMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
    var resourceTradeFractionMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
    var resourceProductionFractionMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
    var resourcePriceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
) {
    /**
     * Get resource quality, default to ResourceQualityData() if the resource doesn't exist
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData {
        return resourceQualityMap[resourceType]?.get(
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
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
    }


    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        val amount: Double = getResourceAmount(resourceType, resourceQualityClass)
        val fraction: Double = resourceTradeFractionMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
        return amount * fraction
    }

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        val amount: Double = getResourceAmount(resourceType, resourceQualityClass)
        val fraction: Double = resourceProductionFractionMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 0.0
        return amount * fraction
    }

    /**
     * Get resource price, default to Double.MAX_VALUE if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
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
) {
    fun updateQuality(
        originalAmount: Double,
        newAmount: Double,
        newData: MutableResourceQualityData
    ) {
        quality1 = (originalAmount * quality1 + newAmount * newData.quality1) /
                (originalAmount + newAmount)
        quality2 = (originalAmount * quality2 + newAmount * newData.quality2) /
                (originalAmount + newAmount)
        quality3 = (originalAmount * quality3 + newAmount * newData.quality3) /
                (originalAmount + newAmount)
    }
}