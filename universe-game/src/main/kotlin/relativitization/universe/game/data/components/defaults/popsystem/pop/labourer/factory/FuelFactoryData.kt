package relativitization.universe.game.data.components.defaults.popsystem.pop.labourer.factory

import ksergen.annotations.GenerateImmutable
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
@GenerateImmutable
data class MutableFuelFactoryData(
    var ownerPlayerId: Int = -1,
    var fuelFactoryInternalData: MutableFuelFactoryInternalData = MutableFuelFactoryInternalData(),
    val maxNumEmployee: Double = 1.0,
    var isOpened: Boolean = true,
    var storedFuelRestMass: Double = 0.0,
    var lastOutputAmount: Double = 0.0,
    var lastNumEmployee: Double = 0.0,
    var experience: Double = 0.0,
)

fun FuelFactoryData.employeeFraction(): Double = if (maxNumEmployee > 0.0) {
    lastNumEmployee / maxNumEmployee
} else {
    0.0
}

fun MutableFuelFactoryData.employeeFraction(): Double = if (maxNumEmployee > 0.0) {
    lastNumEmployee / maxNumEmployee
} else {
    0.0
}

/**
 * Internal Data for a factory of labour pop
 *
 * @property maxOutputAmountPerEmployee maximum output of the factory per employee
 * @property sizePerEmployee the size of this factory per employee
 */
@GenerateImmutable
data class MutableFuelFactoryInternalData(
    var maxOutputAmountPerEmployee: Double = 1.0,
    var sizePerEmployee: Double = 0.0,
)

fun FuelFactoryInternalData.squareDiff(other: FuelFactoryInternalData): Double {
    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputAmountDiff + sizeDiff)
}

fun MutableFuelFactoryInternalData.squareDiff(other: FuelFactoryInternalData): Double {
    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputAmountDiff + sizeDiff)
}

fun FuelFactoryInternalData.squareDiff(other: MutableFuelFactoryInternalData): Double {
    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputAmountDiff + sizeDiff)
}


fun MutableFuelFactoryInternalData.squareDiff(other: MutableFuelFactoryInternalData): Double {
    val outputAmountDiff: Double = (maxOutputAmountPerEmployee - other.maxOutputAmountPerEmployee).pow(2)

    val sizeDiff: Double = (sizePerEmployee - other.sizePerEmployee).pow(2)

    return (outputAmountDiff + sizeDiff)
}