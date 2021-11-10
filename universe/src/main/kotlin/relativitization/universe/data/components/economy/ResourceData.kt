package relativitization.universe.data.components.economy

import kotlinx.serialization.Serializable
import kotlin.math.*

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
     * Get resource price, default to 1.0 if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourcePriceMap[resourceType]?.get(
            resourceQualityClass
        ) ?: 1.0
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
     * Get resource price, default to 1.0 if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double {
        return resourcePriceMap.getOrPut(resourceType) {
            mutableMapOf(resourceQualityClass to 1.0)
        }.getOrPut(resourceQualityClass) {
            1.0
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
}

@Serializable
data class ResourceQualityData(
    val quality1: Double = 0.0,
    val quality2: Double = 0.0,
    val quality3: Double = 0.0,
) {

    operator fun times(d: Double): ResourceQualityData = ResourceQualityData(
        quality1 * d,
        quality2 * d,
        quality3 * d,
    )

    fun square(): Double = quality1 * quality1 + quality2 * quality2 + quality3 * quality3

    fun mag(): Double = sqrt(square())

    fun toMutableResourceQualityData(): MutableResourceQualityData = MutableResourceQualityData(
        quality1,
        quality2,
        quality3
    )


    /**
     * Greater than or equal
     */
    fun geq(other: ResourceQualityData): Boolean {
        return (quality1 >= other.quality1) && (quality2 >= other.quality2) &&
                (quality3 >= other.quality3)
    }

    /**
     * Less than or equal
     */
    fun leq(other: ResourceQualityData): Boolean {
        return (quality1 <= other.quality1) && (quality2 <= other.quality2) &&
                (quality3 <= other.quality3)
    }

    /**
     * Resource difference
     */
    fun squareDiff(other: ResourceQualityData): Double {
        return (quality1 - other.quality1).pow(2) + (quality2 - other.quality2).pow(2) +
                (quality3 - other.quality3).pow(2)
    }

    fun squareDiff(other: MutableResourceQualityData): Double {
        return (quality1 - other.quality1).pow(2) + (quality2 - other.quality2).pow(2) +
                (quality3 - other.quality3).pow(2)
    }
}

@Serializable
data class MutableResourceQualityData(
    var quality1: Double = 0.0,
    var quality2: Double = 0.0,
    var quality3: Double = 0.0,
) {
    operator fun plus(other: MutableResourceQualityData): MutableResourceQualityData = MutableResourceQualityData(
        quality1 + other.quality1,
        quality2 + other.quality2,
        quality3 + other.quality3,
    )

    operator fun minus(other: MutableResourceQualityData): MutableResourceQualityData = MutableResourceQualityData(
        quality1 - other.quality1,
        quality2 - other.quality2,
        quality3 - other.quality3,
    )

    operator fun times(d: Double): MutableResourceQualityData = MutableResourceQualityData(
        quality1 * d,
        quality2 * d,
        quality3 * d,
    )

    fun square(): Double = quality1 * quality1 + quality2 * quality2 + quality3 * quality3

    fun mag(): Double = sqrt(square())

    /**
     * Change the quality data to get closer to other quality
     *
     * @param other the target quality
     * @param changeFactor the factor controlling the change step
     * @param minChange minimum change step
     */
    fun changeTo(
        other: MutableResourceQualityData,
        changeFactor: Double,
        minChange: Double,
    ): MutableResourceQualityData {
        val delta1: MutableResourceQualityData = (other - this) * changeFactor
        val delta2: MutableResourceQualityData = (other - this)
        val delta3: MutableResourceQualityData = MutableResourceQualityData(
            quality1 = 1.0 * delta1.quality1.sign,
            quality2 = 1.0 * delta1.quality2.sign,
            quality3 = 1.0 * delta1.quality3.sign,
        ) * minChange

        val deltaQuality1: Double = if(abs(delta1.quality1) > minChange) {
            delta1.quality1
        } else if (abs(delta2.quality1) > minChange) {
            delta3.quality1
        } else {
            delta2.quality1
        }

        val deltaQuality2: Double = if(abs(delta1.quality2) > minChange) {
            delta1.quality2
        } else if (abs(delta2.quality2) > minChange) {
            delta3.quality2
        } else {
            delta2.quality2
        }

        val deltaQuality3: Double = if(abs(delta1.quality3) > minChange) {
            delta1.quality3
        } else if (abs(delta2.quality3) > minChange) {
            delta3.quality3
        } else {
            delta2.quality3
        }

        return MutableResourceQualityData(
            quality1 + deltaQuality1,
            quality2 + deltaQuality2,
            quality3 + deltaQuality3
        )
    }

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

    /**
     * Resource difference
     */
    fun squareDiff(other: MutableResourceQualityData): Double = (this - other).square()
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