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
 * @property fuelFactoryInternalData the data describing this factory
 * @property numBuilding how large is this factory, affect the throughput of the factory
 * @property isOpened whether this factory is opened
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property lastOutputAmount the output amount in the latest turn
 * @property lastNumEmployee number of employee in the last turn
 */
@Serializable
data class FuelFactoryData(
    val ownerPlayerId: Int = -1,
    val fuelFactoryInternalData: FuelFactoryInternalData = FuelFactoryInternalData(),
    val numBuilding: Int = 1,
    val isOpened: Boolean = true,
    val storedFuelRestMass: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
)

@Serializable
data class MutableFuelFactoryData(
    var ownerPlayerId: Int = -1,
    var fuelFactoryInternalData: MutableFuelFactoryInternalData = MutableFuelFactoryInternalData(),
    var numBuilding: Int = 1,
    var isOpened: Boolean = true,
    var storedFuelRestMass: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
) {
    fun employeeFraction(): Double = lastNumEmployee / (fuelFactoryInternalData.maxNumEmployee * numBuilding)
}

/**
 * Internal Data for a factory of labour pop
 *
 * @property maxOutputAmount maximum output fuel amount
 * @property maxNumEmployee max number of employee
 * @property size the size of this factory
 */
@Serializable
data class FuelFactoryInternalData(
    val maxOutputAmount: Double = 0.0,
    val maxNumEmployee: Double = 0.0,
    val size: Double = 0.0,
) {
    fun squareDiff(other: FuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputAmountDiff + employeeDiff + sizeDiff)
    }

    fun squareDiff(other: MutableFuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputAmountDiff + employeeDiff + sizeDiff)
    }
}

@Serializable
data class MutableFuelFactoryInternalData(
    var maxOutputAmount: Double = 0.0,
    var maxNumEmployee: Double = 0.0,
    var size: Double = 0.0,
) {
    fun squareDiff(other: FuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputAmountDiff + employeeDiff + sizeDiff)
    }

    fun squareDiff(other: MutableFuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmount - other.maxOutputAmount).pow(2)

        val employeeDiff: Double = (maxNumEmployee - other.maxNumEmployee).pow(2)

        val sizeDiff: Double = (size - other.size).pow(2)

        return (outputAmountDiff + employeeDiff + sizeDiff)
    }
}