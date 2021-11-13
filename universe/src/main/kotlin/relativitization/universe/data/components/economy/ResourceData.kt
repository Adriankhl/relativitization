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
 * @property singleResourceMap map from resource type and quality class to SingleResourceData
 */
@Serializable
data class ResourceData(
    val singleResourceMap: Map<ResourceType, Map<ResourceQualityClass, SingleResourceData>> = mapOf(),
) {
    /**
     * Get single resource data, default to SingleResourceData() if it doesn't exist
     */
    fun getSingleResourceData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass,
    ): SingleResourceData = singleResourceMap.getOrDefault(
        resourceType,
        mapOf()
    ).getOrDefault(
        resourceQualityClass,
        SingleResourceData()
    )

    /**
     * Get resource quality
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceQualityData = getSingleResourceData(resourceType, resourceQualityClass).resourceQuality

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceQualityData = getSingleResourceData(resourceType, resourceQualityClass).resourceQualityLowerBound

    /**
     * Get total resource amount data
     */
    fun getResourceAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceAmountData = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount


    /**
     * Get total resource amount
     */
    fun getTotalResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.total()

    /**
     * Get resource storage amount
     */
    fun getStorageResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.storage

    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.trade

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.production

    /**
     * Get resource target amount
     */
    fun getResourceTargetAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceAmountData = getSingleResourceData(resourceType, resourceQualityClass).resourceTargetAmount

    /**
     * Get resource price
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourcePrice
}

@Serializable
data class MutableResourceData(
    val singleResourceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableSingleResourceData>> = mutableMapOf(),
) {
    /**
     * Get single resource data
     */
    fun getSingleResourceData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass,
    ): MutableSingleResourceData = singleResourceMap.getOrPut(resourceType) {
        mutableMapOf()
    }.getOrPut(resourceQualityClass) {
        MutableSingleResourceData()
    }

    /**
     * Get resource quality, default to ResourceQualityData() if the resource doesn't exist
     */
    fun getResourceQuality(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData = getSingleResourceData(resourceType, resourceQualityClass).resourceQuality

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData = getSingleResourceData(resourceType, resourceQualityClass).resourceQualityLowerBound

    /**
     * Get total resource amount data
     */
    fun getResourceAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceAmountData = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount


    /**
     * Get total resource amount, default to 0.0 if the resource doesn't exist
     */
    fun getTotalResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.total()

    /**
     * Get resource storage amount, default to 0.0 if the resource doesn't exist
     */
    fun getStorageResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.storage

    /**
     * Get resource amount available for trading
     */
    fun getTradeResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.trade

    /**
     * Get resource amount available for trading
     */
    fun getProductionResourceAmount(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourceAmount.production


    /**
     * Get resource target amount
     */
    fun getResourceTargetAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceAmountData = getSingleResourceData(resourceType, resourceQualityClass).resourceTargetAmount


    /**
     * Get resource price, default to 1.0 if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourcePrice

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

        getSingleResourceData(resourceType, qualityClass).addNewResource(
            newResourceQuality,
            amount
        )
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

/**
 * The resource data of a specific type and class
 * @property resourceQuality resource quality
 * @property resourceQualityLowerBound the lower bound of resource quality
 * @property resourceAmount resource amount
 * @property resourceTargetAmount  target amount
 * @property resourcePrice resource price in fuel rest mass
 */
@Serializable
data class SingleResourceData(
    val resourceAmount: ResourceAmountData = ResourceAmountData(),
    val resourceTargetAmount: ResourceAmountData = ResourceAmountData(),
    val resourceQuality: ResourceQualityData = ResourceQualityData(),
    val resourceQualityLowerBound: ResourceQualityData = ResourceQualityData(),
    val resourcePrice: Double = 0.01,
)

@Serializable
data class MutableSingleResourceData(
    var resourceAmount: MutableResourceAmountData = MutableResourceAmountData(),
    var resourceTargetAmount: MutableResourceAmountData = MutableResourceAmountData(),
    var resourceQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var resourceQualityLowerBound: MutableResourceQualityData = MutableResourceQualityData(),
    var resourcePrice: Double = 0.01,
) {
    /**
     * Add resource to this data
     */
    fun addNewResource(
        newResourceQuality: MutableResourceQualityData,
        amount: Double,
    ) {
        when {
            resourceAmount.storage < resourceTargetAmount.storage -> {
                val originalAmount: Double = resourceAmount.storage
                val newAmount: Double = originalAmount + amount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.storage = newAmount
            }
            resourceAmount.production < resourceTargetAmount.production -> {
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