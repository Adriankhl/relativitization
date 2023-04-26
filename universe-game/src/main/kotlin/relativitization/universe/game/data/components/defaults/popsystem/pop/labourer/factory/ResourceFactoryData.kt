package relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory

import ksergen.annotations.GenerateImmutable
import relativitization.universe.game.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.game.data.components.defaults.economy.ResourceType
import relativitization.universe.game.data.components.defaults.economy.squareDiff
import kotlin.math.pow

/**
 * Data for a factory of labour pop
 *
 * @property ownerPlayerId the owner of this factory
 * @property resourceFactoryInternalData the data describing this factory
 * @property maxNumEmployee maximum number of employee
 * @property isOpened whether this factory is opened
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property lastOutputAmount the output amount in the latest turn
 * @property lastInputResourceMap the input resource in the latest turn
 * @property lastNumEmployee number of employee in the last turn
 * @property experience the longer the factory has been opening, the more the experience
 */
@GenerateImmutable
data class MutableResourceFactoryData(
    var ownerPlayerId: Int = -1,
    var resourceFactoryInternalData: MutableResourceFactoryInternalData = MutableResourceFactoryInternalData(),
    var maxNumEmployee: Double = 1.0,
    var isOpened: Boolean = true,
    var storedFuelRestMass: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var lastOutputQuality: MutableResourceQualityData = MutableResourceQualityData(),
    val lastInputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var lastNumEmployee: Double = 0.0,
    var experience: Double = 0.0,
)

fun ResourceFactoryData.maxInputAmount(resourceType: ResourceType): Double {
    val amountPerUnit: Double =
        resourceFactoryInternalData.inputResourceMap[resourceType]?.amountPerOutput ?: 0.0
    return amountPerUnit * resourceFactoryInternalData.maxOutputAmountPerEmployee * maxNumEmployee
}

fun MutableResourceFactoryData.maxInputAmount(resourceType: ResourceType): Double {
    val amountPerUnit: Double =
        resourceFactoryInternalData.inputResourceMap[resourceType]?.amountPerOutput ?: 0.0
    return amountPerUnit * resourceFactoryInternalData.maxOutputAmountPerEmployee * maxNumEmployee
}

fun ResourceFactoryData.lastInputAmount(resourceType: ResourceType): Double {
    val amountPerUnit: Double =
        lastInputResourceMap[resourceType]?.amountPerOutput ?: 0.0
    return amountPerUnit * lastOutputAmount
}

fun MutableResourceFactoryData.lastInputAmount(resourceType: ResourceType): Double {
    val amountPerUnit: Double =
        lastInputResourceMap[resourceType]?.amountPerOutput ?: 0.0
    return amountPerUnit * lastOutputAmount
}

fun ResourceFactoryData.employeeFraction(): Double =
    lastNumEmployee / maxNumEmployee

fun MutableResourceFactoryData.employeeFraction(): Double =
    lastNumEmployee / maxNumEmployee


/**
 * Input resource related data
 *
 * @property qualityData maximum input resource quality, quality exceeding this
 *  won't improve the output quality
 * @property amountPerOutput amount of resource required to produce one unit of output resource
 */
@GenerateImmutable
data class MutableInputResourceData(
    var qualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var amountPerOutput: Double = 1.0,
)

fun InputResourceData.squareDiff(other: InputResourceData): Double {
    val qualityDiff: Double =
        qualityData.squareDiff(other.qualityData)

    val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

    return qualityDiff + amountDiff
}

fun MutableInputResourceData.squareDiff(other: InputResourceData): Double {
    val qualityDiff: Double =
        qualityData.squareDiff(other.qualityData)

    val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

    return qualityDiff + amountDiff
}

fun InputResourceData.squareDiff(other: MutableInputResourceData): Double {
    val qualityDiff: Double =
        qualityData.squareDiff(other.qualityData)

    val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

    return qualityDiff + amountDiff
}

fun MutableInputResourceData.squareDiff(other: MutableInputResourceData): Double {
    val qualityDiff: Double =
        qualityData.squareDiff(other.qualityData)

    val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

    return qualityDiff + amountDiff
}

/**
 * Internal Data for a factory of labour pop
 *
 * @property outputResource the output resource type
 * @property maxOutputResourceQualityData maximum output resource quality
 * @property maxOutputAmountPerEmployee maximum output resource amount per employee
 * @property inputResourceMap map the input resource type to the input-related data
 * @property fuelRestMassConsumptionRatePerEmployee fuel consumption rate per employee
 * @property sizePerEmployee the size of this factory per employee
 */
@GenerateImmutable
data class MutableResourceFactoryInternalData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var maxOutputAmountPerEmployee: Double = 1.0,
    var inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRatePerEmployee: Double = 1.0,
    var sizePerEmployee: Double = 0.0,
)

fun ResourceFactoryInternalData.squareDiff(other: ResourceFactoryInternalData): Double {
    val outputResourceDiff: Double = if (outputResource == other.outputResource) {
        0.0
    } else {
        Double.MAX_VALUE * 0.01
    }

    val outputQualityDiff: Double =
        maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val inputDiff: Double = inputResourceMap.map {
        if (!other.inputResourceMap.containsKey(it.key)) {
            Double.MAX_VALUE * 0.01
        } else {
            it.value.squareDiff(other.inputResourceMap.getValue(it.key))
        }
    }.sumOf { it }

    val consumptionDiff: Double =
        (fuelRestMassConsumptionRatePerEmployee - other.fuelRestMassConsumptionRatePerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
            consumptionDiff + sizeDiff)
}

fun MutableResourceFactoryInternalData.squareDiff(other: ResourceFactoryInternalData): Double {
    val outputResourceDiff: Double = if (outputResource == other.outputResource) {
        0.0
    } else {
        Double.MAX_VALUE * 0.01
    }

    val outputQualityDiff: Double =
        maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val inputDiff: Double = inputResourceMap.map {
        if (!other.inputResourceMap.containsKey(it.key)) {
            Double.MAX_VALUE * 0.01
        } else {
            it.value.squareDiff(other.inputResourceMap.getValue(it.key))
        }
    }.sumOf { it }

    val consumptionDiff: Double =
        (fuelRestMassConsumptionRatePerEmployee - other.fuelRestMassConsumptionRatePerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
            consumptionDiff + sizeDiff)
}

fun ResourceFactoryInternalData.squareDiff(other: MutableResourceFactoryInternalData): Double {
    val outputResourceDiff: Double = if (outputResource == other.outputResource) {
        0.0
    } else {
        Double.MAX_VALUE * 0.01
    }

    val outputQualityDiff: Double =
        maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val inputDiff: Double = inputResourceMap.map {
        if (!other.inputResourceMap.containsKey(it.key)) {
            Double.MAX_VALUE * 0.01
        } else {
            it.value.squareDiff(other.inputResourceMap.getValue(it.key))
        }
    }.sumOf { it }

    val consumptionDiff: Double =
        (fuelRestMassConsumptionRatePerEmployee - other.fuelRestMassConsumptionRatePerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
            consumptionDiff + sizeDiff)
}

fun MutableResourceFactoryInternalData.squareDiff(other: MutableResourceFactoryInternalData): Double {
    val outputResourceDiff: Double = if (outputResource == other.outputResource) {
        0.0
    } else {
        Double.MAX_VALUE * 0.01
    }

    val outputQualityDiff: Double =
        maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val inputDiff: Double = inputResourceMap.map {
        if (!other.inputResourceMap.containsKey(it.key)) {
            Double.MAX_VALUE * 0.01
        } else {
            it.value.squareDiff(other.inputResourceMap.getValue(it.key))
        }
    }.sumOf { it }

    val consumptionDiff: Double =
        (fuelRestMassConsumptionRatePerEmployee - other.fuelRestMassConsumptionRatePerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
            consumptionDiff + sizeDiff)
}
