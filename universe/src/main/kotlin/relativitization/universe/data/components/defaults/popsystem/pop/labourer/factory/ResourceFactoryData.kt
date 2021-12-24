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
 * @property numBuilding how large is this factory, affect the throughput of the factory
 * @property isOpened whether this factory is opened
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property lastOutputAmount the output amount in the latest turn
 * @property lastInputResourceMap the input resource in the latest turn
 * @property lastNumEmployee number of employee in the last turn
 */
@Serializable
data class ResourceFactoryData(
    val ownerPlayerId: Int = -1,
    val resourceFactoryInternalData: ResourceFactoryInternalData = ResourceFactoryInternalData(),
    val numBuilding: Double = 1.0,
    val isOpened: Boolean = true,
    val storedFuelRestMass: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val lastOutputQuality: ResourceQualityData = ResourceQualityData(),
    val lastInputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val lastNumEmployee: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double =
            resourceFactoryInternalData.inputResourceMap[resourceType]?.amount ?: 0.0
        return amountPerUnit * resourceFactoryInternalData.maxOutputAmount * numBuilding
    }
}

@Serializable
data class MutableResourceFactoryData(
    var ownerPlayerId: Int = -1,
    var resourceFactoryInternalData: MutableResourceFactoryInternalData = MutableResourceFactoryInternalData(),
    var numBuilding: Double = 1.0,
    var isOpened: Boolean = true,
    var storedFuelRestMass: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var lastOutputQuality: MutableResourceQualityData = MutableResourceQualityData(),
    val lastInputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var lastNumEmployee: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double =
            resourceFactoryInternalData.inputResourceMap[resourceType]?.amount ?: 0.0
        return amountPerUnit * resourceFactoryInternalData.maxOutputAmount * numBuilding
    }

    fun employeeFraction(): Double =
        lastNumEmployee / (resourceFactoryInternalData.maxNumEmployee * numBuilding)
}

/**
 * Input resource related data
 *
 * @property qualityData maximum input resource quality, quality exceeding this
 * won't improve the output quality
 * @property amount amount of resource required to produce one unit of output resource
 */
@Serializable
data class InputResourceData(
    val qualityData: ResourceQualityData = ResourceQualityData(),
    val amount: Double = 1.0,
) {
    fun squareDiff(other: InputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amount - other.amount).pow(2)

        return qualityDiff + amountDiff
    }

    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amount - other.amount).pow(2)

        return qualityDiff + amountDiff
    }
}

@Serializable
data class MutableInputResourceData(
    var qualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var amount: Double = 1.0,
) {
    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double =
            qualityData.squareDiff(other.qualityData)

        val amountDiff: Double = (amount - other.amount).pow(2)

        return qualityDiff + amountDiff
    }
}

/**
 * Internal Data for a factory of labour pop
 *
 * @property outputResource the output resource type
 * @property maxOutputResourceQualityData maximum output resource quality
 * @property maxOutputAmount maximum output resource amount
 * @property inputResourceMap map the input resource type to the input-related data
 * @property fuelRestMassConsumptionRate fuel consumption rate
 * @property maxNumEmployee max number of employee
 * @property size the size of this factory
 */
@Serializable
data class ResourceFactoryInternalData(
    val outputResource: ResourceType = ResourceType.PLANT,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 1.0,
    val maxNumEmployee: Double = 1.0,
    val size: Double = 0.0,
) {
    fun squareDiff(other: ResourceFactoryInternalData): Double {
        val outputResourceDiff: Double = if (outputResource == other.outputResource) {
            0.0
        } else {
            Double.POSITIVE_INFINITY
        }

        val outputQualityDiff: Double =
            maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val inputDiff: Double = inputResourceMap.map {
            if (!other.inputResourceMap.containsKey(it.key)) {
                Double.POSITIVE_INFINITY
            } else {
                it.value.squareDiff(other.inputResourceMap.getValue(it.key))
            }
        }.sumOf { it }

        val consumptionDiff: Double =
            (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }

    fun squareDiff(other: MutableResourceFactoryInternalData): Double {
        val outputResourceDiff: Double = if (outputResource == other.outputResource) {
            0.0
        } else {
            Double.POSITIVE_INFINITY
        }

        val outputQualityDiff: Double =
            maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val inputDiff: Double = inputResourceMap.map {
            if (!other.inputResourceMap.containsKey(it.key)) {
                Double.POSITIVE_INFINITY
            } else {
                it.value.squareDiff(other.inputResourceMap.getValue(it.key))
            }
        }.sumOf { it }

        val consumptionDiff: Double =
            (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }
}

@Serializable
data class MutableResourceFactoryInternalData(
    var outputResource: ResourceType = ResourceType.PLANT,
    var maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var maxOutputAmount: Double = 0.0,
    var inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 1.0,
    var maxNumEmployee: Double = 1.0,
    var size: Double = 0.0,
) {
    fun squareDiff(other: MutableResourceFactoryInternalData): Double {
        val outputResourceDiff: Double = if (outputResource == other.outputResource) {
            0.0
        } else {
            Double.POSITIVE_INFINITY
        }

        val outputQualityDiff: Double =
            maxOutputResourceQualityData.squareDiff(other.maxOutputResourceQualityData)

        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val inputDiff: Double = inputResourceMap.map {
            if (!other.inputResourceMap.containsKey(it.key)) {
                Double.POSITIVE_INFINITY
            } else {
                it.value.squareDiff(other.inputResourceMap.getValue(it.key))
            }
        }.sumOf { it }

        val consumptionDiff: Double =
            (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }
}