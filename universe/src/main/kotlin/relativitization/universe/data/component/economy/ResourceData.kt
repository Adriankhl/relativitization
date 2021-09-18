package relativitization.universe.data.component.economy

import kotlinx.serialization.Serializable

enum class ResourceType(val value: String) {
    FUEL("Fuel"),
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
 * @property resourceQualityLowerBoundMap the lower bound of resource quality
 * @property resourceAmountMap map from ResourceType and ResourceQualityClass to resource amount
 * @property resourceTargetAmountMap  map from ResourceType and ResourceQualityClass to target amount
 * @property resourcePriceMap map from ResourceType and ResourceQualityClass to resource price to
 * fuel rest mass
 */
@Serializable
data class ResourceData(
    val resourceQualityMap: Map<ResourceType, Map<ResourceQualityClass, ResourceQualityData>> = mapOf(),
    val resourceQualityLowerBoundMap: Map<ResourceType, Map<ResourceQualityClass, ResourceQualityData>> = mapOf(),
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
        return resourceQualityMap[resourceType]?.get(
            resourceQualityClass
        ) ?: ResourceQualityData()
    }

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceQualityData {
        return resourceQualityLowerBoundMap[resourceType]?.get(
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
    val resourceQualityMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceQualityData>> = mutableMapOf(),
    val resourceQualityLowerBoundMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceQualityData>> = mutableMapOf(),
    val resourceAmountMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceAmountData>> = mutableMapOf(),
    val resourceTargetAmountMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableResourceAmountData>> = mutableMapOf(),
    val resourcePriceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, Double>> = mutableMapOf(),
) {
    /**
     * Get resource quality, default to ResourceQualityData() if the resource doesn't exist
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData {
        return resourceQualityMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceQualityData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceQualityData()
        }
    }

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData {
        return resourceQualityLowerBoundMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceQualityData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceQualityData()
        }
    }

    /**
     * Get resource amount data
     */
    fun getResourceAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceAmountData {
        return resourceAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }
    }

    /**
     * Get resource amount, default to 0.0 if the resource doesn't exist
     */
    fun getTotalResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }.total()
    }

    /**
     * Get resource storage amount, default to 0.0 if the resource doesn't exist
     */
    fun getStorageResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }.storage
    }

    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }.trade
    }

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourceAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }.production
    }

    /**
     * Get target resource amount data
     */
    fun getResourceTargetAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceAmountData {
        return resourceTargetAmountMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to MutableResourceAmountData())
        }.getOrPut(resourceQualityClass) {
            MutableResourceAmountData()
        }
    }

    /**
     * Get resource price, default to Double.POSITIVE_INFINITY if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourcePriceMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to Double.POSITIVE_INFINITY)
        }.getOrPut(resourceQualityClass) {
            Double.POSITIVE_INFINITY
        }
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

    /**
     * Add resource to storage, production or trading depending on the target
     */
    fun addNewResource(
        resourceType: ResourceType,
        newResourceQuality: MutableResourceQualityData,
        amount: Double,
    ) {
        val qualityClass: ResourceQualityClass = ResourceQualityClass.values().firstOrNull {
            newResourceQuality.geq(getResourceQualityLowerBound(resourceType, it))
        } ?: ResourceQualityClass.THIRD

        val resourceQuality: MutableResourceQualityData = getResourceQuality(
            resourceType,
            qualityClass
        )

        val resourceAmount: MutableResourceAmountData = getResourceAmountData(
            resourceType,
            qualityClass
        )

        val targetResourceAmount: MutableResourceAmountData = getResourceTargetAmountData(
            resourceType,
            qualityClass
        )

        when {
            resourceAmount.storage < targetResourceAmount.storage -> {
                val originalAmount: Double = resourceAmount.storage
                val newAmount: Double = originalAmount + amount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.storage = newAmount
            }
            resourceAmount.production < targetResourceAmount.production -> {
                val originalAmount: Double = resourceAmount.production
                val newAmount: Double = originalAmount + amount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.production = newAmount
            }
            else -> {
                val originalAmount: Double = resourceAmount.trade
                val newAmount: Double = originalAmount + amount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.trade = newAmount
            }
        }
    }

    /**
     * Remove fuel data
     */
    fun removeFuel() {
        resourceQualityMap.remove(ResourceType.FUEL)
        resourceQualityLowerBoundMap.remove(ResourceType.FUEL)
        resourceAmountMap.remove(ResourceType.FUEL)
        resourceTargetAmountMap.remove(ResourceType.FUEL)
        resourcePriceMap.remove(ResourceType.FUEL)
    }

    /**
     * Get fuel amount, ignoring the quality
     */
    fun getFuelAmount(): Double {
        return resourceAmountMap[ResourceType.FUEL]?.values?.sumOf { it.total() } ?: 0.0
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
    fun toResourceQualityData(): ResourceQualityData = ResourceQualityData(
        quality1,
        quality2,
        quality3
    )

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

    operator fun times(d: Double): MutableResourceQualityData = MutableResourceQualityData(
        quality1 * d,
        quality2 * d,
        quality3 * d,
    )
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