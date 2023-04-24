package relativitization.universe.data.components.defaults.popsystem.pop.labourer.factory

import kotlinx.serialization.Serializable
import kotlin.math.pow

/**
 * Data for a factory of labour pop
 *
 * @property ownerPlayerId the owner of this factory
 * @property fuelFactoryInternalData the data describing this factory
 * @property maxNumEmployee the maximum number of employee
 * @property isOpened whether this factory is opened
 * @property storedFuelRestMass stored fuel to be consumed if this is owned by foreign player
 * @property lastOutputAmount the output amount in the latest turn
 * @property lastNumEmployee number of employee in the last turn
 * @property experience the longer the factor has been opening, the more the experience
 */
@Serializable
data class FuelFactoryData(
    val ownerPlayerId: Int = -1,
    val fuelFactoryInternalData: FuelFactoryInternalData = FuelFactoryInternalData(),
    val maxNumEmployee: Double = 1.0,
    val isOpened: Boolean = true,
    val storedFuelRestMass: Double = 0.0,
    val lastOutputAmount: Double = 0.0,
    val lastNumEmployee: Double = 0.0,
    val experience: Double = 0.0,
) {
    fun employeeFraction(): Double = if (maxNumEmployee > 0.0) {
        lastNumEmployee / maxNumEmployee
    } else {
        0.0
    }
}

@Serializable
data class MutableFuelFactoryData(
    var ownerPlayerId: Int = -1,
    var fuelFactoryInternalData: MutableFuelFactoryInternalData = MutableFuelFactoryInternalData(),
    val maxNumEmployee: Double = 1.0,
    var isOpened: Boolean = true,
    var storedFuelRestMass: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
    var experience: Double = 0.0,
) {
    fun employeeFraction(): Double = if (maxNumEmployee > 0.0) {
        lastNumEmployee / maxNumEmployee
    } else {
        0.0
    }
}

/**
 * Internal Data for a factory of labour pop
 *
 * @property maxOutputAmountPerEmployee maximum output of the factory per employee
 * @property sizePerEmployee the size of this factory per employee
 */
@Serializable
data class FuelFactoryInternalData(
    val maxOutputAmountPerEmployee: Double = 1.0,
    val sizePerEmployee: Double = 0.0,
) {
    fun squareDiff(other: FuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

        val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

        return (outputAmountDiff + sizeDiff)
    }

    fun squareDiff(other: MutableFuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

        val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

        return (outputAmountDiff + sizeDiff)
    }
}

@Serializable
data class MutableFuelFactoryInternalData(
    var maxOutputAmountPerEmployee: Double = 1.0,
    var sizePerEmployee: Double = 0.0,
) {
    fun squareDiff(other: FuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

        val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

        return (outputAmountDiff + sizeDiff)
    }

    fun squareDiff(other: MutableFuelFactoryInternalData): Double {
        val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

        val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

        return (outputAmountDiff + sizeDiff)
    }
}