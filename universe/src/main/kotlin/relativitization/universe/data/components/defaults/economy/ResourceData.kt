package relativitization.universe.data.components.defaults.economy

import kotlinx.serialization.Serializable
import relativitization.universe.utils.RelativitizationLogManager
import kotlin.math.*

enum class ResourceType(val value: String) {
    PLANT("Plant"), // Raw material
    ANIMAL("Animal"),
    METAL("Metal"),
    PLASTIC("Plastic"),
    FOOD("Food"), // Final product
    CLOTH("Cloth"),
    HOUSEHOLD_GOOD("Household good"),
    RESEARCH_EQUIPMENT("Research equipment"),
    MEDICINE("Medicine"),
    AMMUNITION("Ammunition"),
    ENTERTAINMENT("Entertainment"),
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
    val singleResourceMap: Map<ResourceType, Map<ResourceQualityClass, SingleResourceData>> =
        ResourceType.values().map { resourceType ->
            resourceType to ResourceQualityClass.values().map { resourceQualityClass ->
                resourceQualityClass to SingleResourceData()
            }.toMap()
        }.toMap(),
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
    ): ResourceQualityData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceQuality

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceQualityData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceQualityLowerBound

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
    ): ResourceAmountData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceTargetAmount

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
    val singleResourceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableSingleResourceData>> =
        ResourceType.values().map { resourceType ->
            resourceType to ResourceQualityClass.values().map { resourceQualityClass ->
                resourceQualityClass to MutableSingleResourceData()
            }.toMap().toMutableMap()
        }.toMap().toMutableMap(),
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
    ): MutableResourceQualityData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceQuality

    /**
     * Get resource quality lower bound
     */
    fun getResourceQualityLowerBound(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceQualityData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceQualityLowerBound

    /**
     * Get total resource amount data
     */
    fun getResourceAmountData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceAmountData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceAmount


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
    ): MutableResourceAmountData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceTargetAmount


    /**
     * Get resource price, default to 1.0 if the resource doesn't exist
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourcePrice

    /**
     * Get resource quality class with target quality and amount for production
     * Default to quality class with maximum amount if none of them satisfy the requirement
     *
     * @param resourceType the type of required resource
     * @param amount the target amount to get
     * @param targetQuality the target quality of the resource to get
     * @param preferHighQualityClass prefer high quality class that satisfy the requirement,
     * prefer low quality class if false
     */
    fun productionQualityClass(
        resourceType: ResourceType,
        amount: Double,
        targetQuality: MutableResourceQualityData,
        preferHighQualityClass: Boolean,
    ): ResourceQualityClass {
        val satisfyList: List<Pair<ResourceQualityClass, Boolean>> =
            ResourceQualityClass.values().toList().map {
                val b1: Boolean = getResourceQuality(resourceType, it).geq(targetQuality)
                val b2: Boolean = getProductionResourceAmount(resourceType, it) >= amount
                it to (b1 && b2)
            }
        return if (preferHighQualityClass) {
            satisfyList.firstOrNull { it.second }?.first ?: run {
                ResourceQualityClass.values().maxByOrNull {
                    getProductionResourceAmount(resourceType, it)
                } ?: ResourceQualityClass.THIRD
            }
        } else {
            satisfyList.lastOrNull { it.second }?.first ?: run {
                ResourceQualityClass.values().maxByOrNull {
                    getProductionResourceAmount(resourceType, it)
                } ?: ResourceQualityClass.THIRD
            }
        }
    }

    /**
     * Get resource quality class with target quality, amount and budget for trade
     * Default to quality class with maximum amount if none of them satisfy the requirement
     *
     * @param resourceType the type of required resource
     * @param amount the target amount to get
     * @param targetQuality the target quality of the resource to get
     * @param budget the budget to buy this resource
     * @param preferHighQualityClass prefer high quality class that satisfy the requirement,
     * prefer low quality class if false
     */
    fun tradeQualityClass(
        resourceType: ResourceType,
        amount: Double,
        targetQuality: MutableResourceQualityData,
        budget: Double,
        preferHighQualityClass: Boolean,
    ): ResourceQualityClass {
        val satisfyList: List<Pair<ResourceQualityClass, Boolean>> =
            ResourceQualityClass.values().toList().map {
                val b1: Boolean = getResourceQuality(resourceType, it).geq(targetQuality)
                val b2: Boolean = getTradeResourceAmount(resourceType, it) >= amount
                val b3: Boolean = budget >= getResourcePrice(resourceType, it) * amount
                it to (b1 && b2 && b3)
            }
        return if (preferHighQualityClass) {
            satisfyList.firstOrNull { it.second }?.first ?: run {
                ResourceQualityClass.values().maxByOrNull {
                    getTradeResourceAmount(resourceType, it)
                } ?: ResourceQualityClass.THIRD
            }
        } else {
            satisfyList.lastOrNull { it.second }?.first ?: run {
                ResourceQualityClass.values().maxByOrNull {
                    getTradeResourceAmount(resourceType, it)
                } ?: ResourceQualityClass.THIRD
            }
        }
    }


    /**
     * Add resource to storage, production or trading depending on the target
     */
    fun addResource(
        newResourceType: ResourceType,
        newResourceQuality: MutableResourceQualityData,
        newResourceAmount: Double,
    ) {
        val qualityClass: ResourceQualityClass = ResourceQualityClass.values().firstOrNull {
            newResourceQuality.geq(getResourceQualityLowerBound(newResourceType, it))
        } ?: ResourceQualityClass.THIRD

        getSingleResourceData(newResourceType, qualityClass).addResource(
            newResourceQuality,
            newResourceAmount
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
    operator fun plus(other: MutableResourceQualityData): MutableResourceQualityData =
        MutableResourceQualityData(
            quality1 + other.quality1,
            quality2 + other.quality2,
            quality3 + other.quality3,
        )

    operator fun plus(num: Double): MutableResourceQualityData =
        MutableResourceQualityData(
            quality1 + num,
            quality2 + num,
            quality3 + num,
        )

    operator fun minus(other: MutableResourceQualityData): MutableResourceQualityData =
        MutableResourceQualityData(
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

        val deltaQuality1: Double = if (abs(delta1.quality1) > minChange) {
            delta1.quality1
        } else if (abs(delta2.quality1) > minChange) {
            delta3.quality1
        } else {
            delta2.quality1
        }

        val deltaQuality2: Double = if (abs(delta1.quality2) > minChange) {
            delta1.quality2
        } else if (abs(delta2.quality2) > minChange) {
            delta3.quality2
        } else {
            delta2.quality2
        }

        val deltaQuality3: Double = if (abs(delta1.quality3) > minChange) {
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
        if (originalAmount + newAmount > 0.0) {
            quality1 = (originalAmount * quality1 + newAmount * newData.quality1) /
                    (originalAmount + newAmount)
            quality2 = (originalAmount * quality2 + newAmount * newData.quality2) /
                    (originalAmount + newAmount)
            quality3 = (originalAmount * quality3 + newAmount * newData.quality3) /
                    (originalAmount + newAmount)
        } else {
            logger.debug("Add 0 new resource to 0 original resource")
        }
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

    /**
     * Combine min of the qualities
     */
    fun combineMin(other: MutableResourceQualityData): MutableResourceQualityData {
        return MutableResourceQualityData(
            quality1 = min(quality1, other.quality1),
            quality2 = min(quality2, other.quality2),
            quality3 = min(quality3, other.quality3)
        )
    }

    /**
     * Combine max of the qualities
     */
    fun combineMax(other: MutableResourceQualityData): MutableResourceQualityData {
        return MutableResourceQualityData(
            quality1 = max(quality1, other.quality1),
            quality2 = max(quality2, other.quality2),
            quality3 = max(quality3, other.quality3)
        )
    }

    companion object {
        val logger = RelativitizationLogManager.getLogger()
    }
}

/**
 * Amount of resource in different usage
 *
 * @property storage not for use
 * @property production for production
 * @property trade for trade
 */
@Serializable
data class ResourceAmountData(
    val storage: Double = 0.0,
    val production: Double = 0.0,
    val trade: Double = 0.0,
) {
    fun total(): Double = storage + trade + production
}

@Serializable
data class MutableResourceAmountData(
    var storage: Double = 0.0,
    var production: Double = 0.0,
    var trade: Double = 0.0,
) {
    operator fun times(num: Double) = MutableResourceAmountData(
        storage = storage * num,
        production = production * num,
        trade = trade * num
    )

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
    fun addResource(
        newResourceQuality: MutableResourceQualityData,
        newResourceAmount: Double,
    ) {
        when {
            resourceAmount.storage < resourceTargetAmount.storage -> {
                val originalAmount: Double = resourceAmount.storage
                val newAmount: Double = originalAmount + newResourceAmount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.storage = newAmount
            }
            resourceAmount.production < resourceTargetAmount.production -> {
                val originalAmount: Double = resourceAmount.production
                val newAmount: Double = originalAmount + newResourceAmount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.production = newAmount
            }
            else -> {
                val originalAmount: Double = resourceAmount.trade
                val newAmount: Double = originalAmount + newResourceAmount
                resourceQuality.updateQuality(originalAmount, newAmount, newResourceQuality)
                resourceAmount.trade = newAmount
            }
        }
    }
}