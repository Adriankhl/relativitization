package relativitization.universe.data.component.economy

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
 * @property resourceTargetAmountMap  map from ResourceType and ResourceQualityClass to target amount
 * @property resourcePriceMap map from ResourceType and ResourceQualityClass to resource price to
 * fuel rest mass
 */
@Serializable
data class ResourceData(
    val resourceQualityMap: Map<ResourceType, Map<ResourceQualityClass, ResourceQualityData>> = mapOf(),
    val resourceAmountMap: Map<ResourceType, Map<ResourceQualityClass, ResourceAmountData>> = mapOf(),
    val resourceTargetAmountMap: Map<ResourceType, Map<ResourceQualityClass, ResourceAmountData>> = mapOf(),
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
     * Get total resource amount, default to 0.0 if the resource doesn't exist
     */
    fun getTotalResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.total() ?: 0.0
    }

    /**
     * Get resource storage amount, default to 0.0 if the resource doesn't exist
     */
    fun getStorageResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.storage ?: 0.0
    }

    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.trade ?: 0.0
    }

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.production ?: 0.0
    }


    /**
     * Get resource price, default to Double.POSITIVE_INFINITY if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourcePriceMap[resourceType]?.get(
            resourceQualityClass
        ) ?: Double.POSITIVE_INFINITY
    }
}

@Serializable
data class MutableResourceData(
    var resourceQualityMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceQualityData>> = mutableMapOf(),
    val resourceAmountMap: MutableMap<ResourceType, Map<ResourceQualityClass, MutableResourceAmountData>> = mutableMapOf(),
    val resourceTargetAmountMap: MutableMap<ResourceType, Map<ResourceQualityClass, MutableResourceAmountData>> = mutableMapOf(),
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
    fun getTotalResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.total() ?: 0.0
    }

    /**
     * Get resource storage amount, default to 0.0 if the resource doesn't exist
     */
    fun getStorageResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.storage ?: 0.0
    }

    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.trade ?: 0.0
    }

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap[resourceType]?.get(
            resourceQualityClass
        )?.trade ?: 0.0
    }

    /**
     * Get resource price, default to Double.POSITIVE_INFINITY if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourcePriceMap[resourceType]?.get(
            resourceQualityClass
        ) ?: Double.POSITIVE_INFINITY
    }

    /**
     * Get resource quality class with target quality and amount
     * Default to quality class with maximum amount if none of them satisfy the requirement
     */
    fun productionQualityClass(
        resourceType: ResourceType,
        amount: Double,
        targetQuality: MutableResourceQualityData,
    ): ResourceQualityClass {
        val satisfyList: List<Pair<ResourceQualityClass, Boolean>> = ResourceQualityClass.values().toList().map {
            val b1: Boolean = getResourceQuality(resourceType, it).geq(targetQuality)
            val b2: Boolean = getProductionResourceAmount(resourceType, it) >= amount
            it to (b1 && b2)
        }
        return satisfyList.firstOrNull { it.second }?.first ?: run {
            ResourceQualityClass.values().maxByOrNull {
                getProductionResourceAmount(resourceType, it)
            } ?: ResourceQualityClass.THIRD
        }
    }
}

@Serializable
data class ResourceQualityData(
    val quality1: Double = 0.0,
    val quality2: Double = 0.0,
    val quality3: Double = 0.0,
) {
    /**
     * Greater than or equal
     */
    fun geq(other: ResourceQualityData): Boolean {
        return (quality1 >= other.quality1) && (quality2 >= other.quality2) &&
                (quality3 >= other.quality3)
    }
/** * Less than or equal
     */
    fun leq(other: ResourceQualityData): Boolean {
        return (quality1 <= other.quality1) && (quality2 <= other.quality2) &&
                (quality3 <= other.quality3)
    }
}

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

    /**
     * Greater than or equal
     */
    fun geq(other: MutableResourceQualityData): Boolean {
        return (quality1 >= other.quality1) && (quality2 >= other.quality2) &&
                (quality3 >= other.quality3)
    }

    /**
     * Less than or equal
     */
    fun leq(other: MutableResourceQualityData): Boolean {
        return (quality1 <= other.quality1) && (quality2 <= other.quality2) &&
                (quality3 <= other.quality3)
    }
}

/**
 * Amount of resource in different usage
 *
 * @property storage not for use
 * @property trade for trade
 * @property production for production
 */
@Serializable
data class ResourceAmountData(
    val storage: Double = 0.0,
    val trade: Double = 0.0,
    val production: Double = 0.0,
) {
    fun total(): Double = storage + trade + production
}

@Serializable
data class MutableResourceAmountData(
    var storage: Double = 0.0,
    var trade: Double = 0.0,
    var production: Double = 0.0,
) {
    fun total(): Double = storage + trade + production
}