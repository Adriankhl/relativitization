package relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.economy.MutableResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceQualityData
import relativitization.universe.data.components.defaults.economy.ResourceType
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
@Serializable
data class ResourceFactoryData(
    val ownerPlayerId: Int = -1,
    val resourceFactoryInternalData: ResourceFactoryInternalData = ResourceFactoryInternalData(),
    val maxNumEmployee: Double = 1.0,
    val isOpened: Boolean = true,
    val storedFuelRestMass: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val lastOutputQuality: ResourceQualityData = ResourceQualityData(),
    val lastInputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val lastNumEmployee: Double = 0.0,
    val experience: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double =
            resourceFactoryInternalData.inputResourceMap[resourceType]?.amountPerOutput ?: 0.0
        return amountPerUnit * resourceFactoryInternalData.maxOutputAmountPerEmployee * maxNumEmployee
    }

    fun lastInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double =
            lastInputResourceMap[resourceType]?.amountPerOutput ?: 0.0
        return amountPerUnit * lastOutputAmount
    }

    fun employeeFraction(): Double =
        lastNumEmployee / maxNumEmployee
}

@Serializable
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
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double =
            resourceFactoryInternalData.inputResourceMap[resourceType]?.amountPerOutput ?: 0.0
        return amountPerUnit * resourceFactoryInternalData.maxOutputAmountPerEmployee * maxNumEmployee
    }

    fun employeeFraction(): Double =
        lastNumEmployee / maxNumEmployee
}

/**
 * Input resource related data
 *
 * @property qualityData maximum input resource quality, quality exceeding this
 * won't improve the output quality
 * @property amountPerOutput amount of resource required to produce one unit of output resource
 */
@Serializable
data class InputResourceData(
    val qualityData: ResourceQualityData = ResourceQualityData(),
    val amountPerOutput: Double = 1.0,
) {
    fun squareDiff(other: InputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

        return qualityDiff + amountDiff
    }

    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

        return qualityDiff + amountDiff
    }
}

@Serializable
data class MutableInputResourceData(
    var qualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var amountPerOutput: Double = 1.0,
) {
    fun squareDiff(other: InputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

        return qualityDiff + amountDiff
    }

    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amountPerOutput - other.amountPerOutput).pow(2)

        return qualityDiff + amountDiff
    }
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
@Serializable
data class ResourceFactoryInternalData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmountPerEmployee: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRatePerEmployee: Double = 1.0,
    val sizePerEmployee: Double = 0.0,
) {
    fun squareDiff(other: ResourceFactoryInternalData): Double {
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

    fun squareDiff(other: MutableResourceFactoryInternalData): Double {
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
}

@Serializable
data class MutableResourceFactoryInternalData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var maxOutputAmountPerEmployee: Double = 1.0,
    var inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRatePerEmployee: Double = 1.0,
    var sizePerEmployee: Double = 0.0,
) {
    fun squareDiff(other: ResourceFactoryInternalData): Double {
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

    fun squareDiff(other: MutableResourceFactoryInternalData): Double {
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
}