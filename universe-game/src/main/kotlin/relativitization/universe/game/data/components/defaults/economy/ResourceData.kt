package relativitization.universe.game.data.components.defaults.economy

import kotlinx.serialization.Serializable
import relativitization.universe.game.utils.RelativitizationLogManager
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.sqrt

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

    companion object {
        // Entertainment is not produced by factory
        val factoryResourceList: List<ResourceType> = values().toList() - ENTERTAINMENT

        private val productionResourceList: List<ResourceType> = listOf(
            PLANT,
            ANIMAL,
            METAL,
            PLASTIC,
            RESEARCH_EQUIPMENT,
        )

        // whether the resource is involved in production
        fun isProductionResource(resourceType: ResourceType): Boolean = productionResourceList.contains(resourceType)
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
        ResourceType.values().associateWith { resourceType ->
            ResourceQualityClass.values().associateWith {
                val resourceTargetProportion: ResourceTargetProportionData =
                    if (ResourceType.isProductionResource(resourceType)) {
                        ResourceTargetProportionData(
                            storage = 0.25,
                            production = 0.5,
                            trade = 0.25
                        )
                    } else {
                        ResourceTargetProportionData(
                            storage = 0.5,
                            production = 0.0,
                            trade = 0.5
                        )
                    }

                SingleResourceData(resourceTargetProportion = resourceTargetProportion)
            }
        },
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
    fun getResourceTargetProportionData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): ResourceTargetProportionData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceTargetProportion

    /**
     * Get resource price
     */
    fun getResourcePrice(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): Double = getSingleResourceData(resourceType, resourceQualityClass).resourcePrice

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
     * @param tariffFactor if the trade is affected by tariff
     */
    fun tradeQualityClass(
        resourceType: ResourceType,
        amount: Double,
        targetQuality: ResourceQualityData,
        budget: Double,
        preferHighQualityClass: Boolean,
        tariffFactor: Double = 1.0,
    ): ResourceQualityClass {
        val satisfyList: List<Pair<ResourceQualityClass, Boolean>> =
            ResourceQualityClass.values().toList().map {
                val b1: Boolean = getResourceQuality(resourceType, it).geq(targetQuality)
                val b2: Boolean = getTradeResourceAmount(resourceType, it) >= amount
                val b3: Boolean = budget >= getResourcePrice(resourceType, it) * amount * tariffFactor
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

}

@Serializable
data class MutableResourceData(
    val singleResourceMap: MutableMap<ResourceType, MutableMap<ResourceQualityClass, MutableSingleResourceData>> =
        ResourceType.values().associateWith { resourceType ->
            ResourceQualityClass.values().associateWith {
                val resourceTargetProportion: MutableResourceTargetProportionData =
                    if (ResourceType.isProductionResource(resourceType)) {
                        MutableResourceTargetProportionData(
                            storage = 0.25,
                            production = 0.5,
                            trade = 0.25
                        )
                    } else {
                        MutableResourceTargetProportionData(
                            storage = 0.5,
                            production = 0.0,
                            trade = 0.5
                        )
                    }

                MutableSingleResourceData(resourceTargetProportion = resourceTargetProportion)
            }.toMutableMap()
        }.toMutableMap(),
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
    fun getResourceTargetProportionData(
        resourceType: ResourceType,
        resourceQualityClass: ResourceQualityClass
    ): MutableResourceTargetProportionData =
        getSingleResourceData(resourceType, resourceQualityClass).resourceTargetProportion


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
     * @param tariffFactor if the trade is affected by tariff
     */
    fun tradeQualityClass(
        resourceType: ResourceType,
        amount: Double,
        targetQuality: MutableResourceQualityData,
        budget: Double,
        preferHighQualityClass: Boolean,
        tariffFactor: Double = 1.0,
    ): ResourceQualityClass {
        val satisfyList: List<Pair<ResourceQualityClass, Boolean>> =
            ResourceQualityClass.values().toList().map {
                val b1: Boolean = getResourceQuality(resourceType, it).geq(targetQuality)
                val b2: Boolean = getTradeResourceAmount(resourceType, it) >= amount
                val b3: Boolean = budget >= getResourcePrice(resourceType, it) * amount * tariffFactor
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
    val quality: Double = 0.0,
) {
    operator fun plus(other: ResourceQualityData): ResourceQualityData =
        ResourceQualityData(
            quality + other.quality,
        )


    operator fun plus(other: MutableResourceQualityData): ResourceQualityData =
        ResourceQualityData(
            quality + other.quality,
        )

    operator fun plus(num: Double): ResourceQualityData =
        ResourceQualityData(
            quality + num,
        )

    operator fun times(d: Double): ResourceQualityData = ResourceQualityData(
        quality * d,
    )

    /**
     * If quality equals to zero, the output equal to zero instead of undefined
     */
    operator fun div(d: Double): ResourceQualityData = ResourceQualityData(
        if (quality != 0.0) {
            quality / d
        } else {
            0.0
        }
    )

    fun square(): Double = quality * quality

    fun mag(): Double = sqrt(square())

    fun toMutableResourceQualityData(): MutableResourceQualityData = MutableResourceQualityData(
        quality,
    )


    /**
     * Greater than or equal
     */
    fun geq(other: ResourceQualityData): Boolean {
        return (quality >= other.quality)
    }

    /**
     * Less than or equal
     */
    fun leq(other: ResourceQualityData): Boolean {
        return (quality <= other.quality)
    }

    /**
     * Resource difference
     */
    fun squareDiff(other: ResourceQualityData): Double {
        return (quality - other.quality).pow(2)
    }

    fun squareDiff(other: MutableResourceQualityData): Double {
        return (quality - other.quality).pow(2)
    }
}

@Serializable
data class MutableResourceQualityData(
    var quality: Double = 0.0,
) {
    operator fun plus(other: MutableResourceQualityData): MutableResourceQualityData =
        MutableResourceQualityData(
            quality + other.quality,
        )

    operator fun plus(num: Double): MutableResourceQualityData =
        MutableResourceQualityData(
            quality + num,
        )

    operator fun minus(other: MutableResourceQualityData): MutableResourceQualityData =
        MutableResourceQualityData(
            quality - other.quality,
        )

    operator fun times(d: Double): MutableResourceQualityData = MutableResourceQualityData(
        quality * d,
    )

    /**
     * If quality equals to zero, the output equal to zero instead of undefined
     */
    operator fun div(d: Double): MutableResourceQualityData = MutableResourceQualityData(
        if (quality != 0.0) {
            quality / d
        } else {
            0.0
        },
    )

    fun square(): Double = quality * quality

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
            quality = 1.0 * delta1.quality.sign,
        ) * minChange

        val deltaQuality1: Double = if (abs(delta1.quality) > minChange) {
            delta1.quality
        } else if (abs(delta2.quality) > minChange) {
            delta3.quality
        } else {
            delta2.quality
        }

        return MutableResourceQualityData(
            quality + deltaQuality1,
        )
    }

    fun toResourceQualityData(): ResourceQualityData = ResourceQualityData(
        quality,
    )

    fun updateQuality(
        originalAmount: Double,
        newAmount: Double,
        newData: MutableResourceQualityData
    ) {
        if (originalAmount + newAmount > 0.0) {
            quality = (originalAmount * quality + newAmount * newData.quality) /
                    (originalAmount + newAmount)
        } else {
            logger.debug("Add 0 new resource to 0 original resource")
        }
    }

    /**
     * Greater than or equal
     */
    fun geq(other: MutableResourceQualityData): Boolean {
        return (quality >= other.quality)
    }

    /**
     * Less than or equal
     */
    fun leq(other: MutableResourceQualityData): Boolean {
        return (quality <= other.quality)
    }

    /**
     * Minimum of this or other
     */
    fun min(other: MutableResourceQualityData): MutableResourceQualityData {
        return MutableResourceQualityData(
            min(this.quality, other.quality)
        )
    }

    /**
     * Resource difference
     */
    fun squareDiff(other: ResourceQualityData): Double {
        return (quality - other.quality).pow(2)
    }

    fun squareDiff(other: MutableResourceQualityData): Double {
        return (quality - other.quality).pow(2)
    }

    /**
     * Combine min of the qualities
     */
    fun combineMin(other: MutableResourceQualityData): MutableResourceQualityData {
        return MutableResourceQualityData(
            quality = min(quality, other.quality),
        )
    }

    /**
     * Combine max of the qualities
     */
    fun combineMax(other: MutableResourceQualityData): MutableResourceQualityData {
        return MutableResourceQualityData(
            quality = max(quality, other.quality),
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
 * Target proportion of resource
 *
 * @property storage not for use
 * @property production for production
 * @property trade for trade
 */
@Serializable
data class ResourceTargetProportionData(
    val storage: Double = 0.5,
    val production: Double = 0.0,
    val trade: Double = 0.5,
) {
    fun total(): Double = storage + trade + production
}

@Serializable
data class MutableResourceTargetProportionData(
    var storage: Double = 0.5,
    var production: Double = 0.0,
    var trade: Double = 0.5,
) {
    fun total(): Double = storage + trade + production
}

/**
 * The resource data of a specific type and class
 * @property resourceAmount resource amount
 * @property resourceTargetProportion the target proportion of the resource categories
 * @property resourceQuality resource quality
 * @property resourceQualityLowerBound the lower bound of resource quality
 * @property resourcePrice resource price in fuel rest mass
 */
@Serializable
data class SingleResourceData(
    val resourceAmount: ResourceAmountData = ResourceAmountData(),
    val resourceTargetProportion: ResourceTargetProportionData = ResourceTargetProportionData(),
    val resourceQuality: ResourceQualityData = ResourceQualityData(),
    val resourceQualityLowerBound: ResourceQualityData = ResourceQualityData(),
    val resourcePrice: Double = 1E-6,
)

@Serializable
data class MutableSingleResourceData(
    var resourceAmount: MutableResourceAmountData = MutableResourceAmountData(),
    var resourceTargetProportion: MutableResourceTargetProportionData = MutableResourceTargetProportionData(),
    var resourceQuality: MutableResourceQualityData = MutableResourceQualityData(),
    var resourceQualityLowerBound: MutableResourceQualityData = MutableResourceQualityData(),
    var resourcePrice: Double = 1E-6,
) {
    /**
     * Add resource to this data
     */
    fun addResource(
        newResourceQuality: MutableResourceQualityData,
        newResourceAmount: Double,
    ) {
        val totalResource: Double = newResourceAmount + resourceAmount.total()
        val totalTargetWeight: Double = resourceTargetProportion.total()

        val targetStorage: Double = if (totalTargetWeight > 0.0) {
            resourceTargetProportion.storage / totalTargetWeight * totalResource
        } else {
            totalResource / 3.0
        }

        val targetProduction: Double = if (totalTargetWeight > 0.0) {
            resourceTargetProportion.production / totalTargetWeight * totalResource
        } else {
            totalResource / 3.0
        }

        if (newResourceAmount > 0.0) {
            when {
                resourceAmount.storage < targetStorage -> {
                    val actualResourceAdded: Double = min(
                        newResourceAmount,
                        targetStorage - resourceAmount.storage,
                    )
                    resourceQuality.updateQuality(resourceAmount.total(), actualResourceAdded, newResourceQuality)
                    resourceAmount.storage += actualResourceAdded
                    addResource(newResourceQuality, newResourceAmount - actualResourceAdded)
                }
                resourceAmount.production < targetProduction -> {
                    val actualResourceAdded: Double = min(
                        newResourceAmount,
                        targetProduction - resourceAmount.production,
                    )
                    resourceQuality.updateQuality(resourceAmount.total(), actualResourceAdded, newResourceQuality)
                    resourceAmount.production += actualResourceAdded
                    addResource(newResourceQuality, newResourceAmount - actualResourceAdded)
                }
                else -> {
                    resourceQuality.updateQuality(resourceAmount.total(), newResourceAmount, newResourceQuality)
                    resourceAmount.trade += newResourceAmount
                }
            }
        }
    }
}