package relativitization.universe.data.component.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import relativitization.universe.data.component.economy.MutableResourceQualityData
import relativitization.universe.data.component.economy.ResourceQualityData
import relativitization.universe.data.component.economy.ResourceType
import kotlin.math.pow

/**
 * Data for a factory of labour pop
 *
 * @property ownerPlayerId the owner of this factory
 * @property factoryInternalData the data describing this factory
 * @property numBuilding how large is this factory, affect the throughput of the factory
 * @property isOpened whether this factory is opened
 * @property lastOutputAmount the output amount in the latest turn
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property lastNumEmployee number of employee in the last turn
 */
@Serializable
data class FactoryData(
    val ownerPlayerId: Int = -1,
    val factoryInternalData: FactoryInternalData = FactoryInternalData(),
    val numBuilding: Int = 1,
    val isOpened: Boolean = true,
    val lastOutputAmount: Double = 0.0,
    val lastInputAmountMap: Map<ResourceType, Double> = mapOf(),
    val storedFuelRestMass: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double = factoryInternalData.inputResourceMap[resourceType]?.amountPerOutputUnit ?: 0.0
        return amountPerUnit * factoryInternalData.maxOutputAmount * numBuilding
    }
}

@Serializable
data class MutableFactoryData(
    var ownerPlayerId: Int = -1,
    var factoryInternalData: MutableFactoryInternalData = MutableFactoryInternalData(),
    var numBuilding: Int = 1,
    var isOpened: Boolean = true,
    var lastOutputAmount: Double = 0.0,
    val lastInputAmountMap: MutableMap<ResourceType, Double> = mutableMapOf(),
    var storedFuelRestMass: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
) {
    fun maxInputAmount(resourceType: ResourceType): Double {
        val amountPerUnit: Double = factoryInternalData.inputResourceMap[resourceType]?.amountPerOutputUnit ?: 0.0
        return amountPerUnit * factoryInternalData.maxOutputAmount * numBuilding
    }
}

/**
 * Input resource related data
 *
 * @property maxInputResourceQualityData maximum input resource quality, quality exceeding this
 * won't improve the output quality
 * @property amountPerOutputUnit amount of resource required to produce one unit of output resource
 */
@Serializable
data class InputResourceData(
    val maxInputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val amountPerOutputUnit: Double = 1.0,
) {
    fun squareDiff(other: InputResourceData): Double {
        val qualityDiff: Double = maxInputResourceQualityData.squareDiff(other.maxInputResourceQualityData)

        val amountDiff: Double = (amountPerOutputUnit - other.amountPerOutputUnit).pow(2)

        return qualityDiff + amountDiff
    }

    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double = maxInputResourceQualityData.squareDiff(other.maxInputResourceQualityData)

        val amountDiff: Double = (amountPerOutputUnit - other.amountPerOutputUnit).pow(2)

        return qualityDiff + amountDiff
    }
}

@Serializable
data class MutableInputResourceData(
    var maxInputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var amountPerOutputUnit: Double = 1.0,
) {
    fun squareDiff(other: MutableInputResourceData): Double {
        val qualityDiff: Double = maxInputResourceQualityData.squareDiff(other.maxInputResourceQualityData)

        val amountDiff: Double = (amountPerOutputUnit - other.amountPerOutputUnit).pow(2)

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
data class FactoryInternalData(
    val outputResource: ResourceType = ResourceType.FUEL,
    val maxOutputResourceQualityData: ResourceQualityData = ResourceQualityData(),
    val maxOutputAmount: Double = 0.0,
    val inputResourceMap: Map<ResourceType, InputResourceData> = mapOf(),
    val fuelRestMassConsumptionRate: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val size: Double = 0.0,
) {
    fun squareDiff(other: FactoryInternalData): Double {
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

        val consumptionDiff: Double = (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }

    fun squareDiff(other: MutableFactoryInternalData): Double {
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

        val consumptionDiff: Double = (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }
}

@Serializable
data class MutableFactoryInternalData(
    var outputResource: ResourceType = ResourceType.FUEL,
    var maxOutputResourceQualityData: MutableResourceQualityData = MutableResourceQualityData(),
    var maxOutputAmount: Double = 0.0,
    var inputResourceMap: MutableMap<ResourceType, MutableInputResourceData> = mutableMapOf(),
    var fuelRestMassConsumptionRate: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var size: Double = 0.0,
) {
    fun squareDiff(other: MutableFactoryInternalData): Double {
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

        val consumptionDiff: Double = (fuelRestMassConsumptionRate - other.fuelRestMassConsumptionRate).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputResourceDiff + outputQualityDiff + outputAmountDiff + inputDiff +
                consumptionDiff + employeeDiff + sizeDiff)
    }
}