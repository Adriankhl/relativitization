package relativitization.universe.data.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.component.physics.FuelRestMassData
import relativitization.universe.data.component.physics.MutableFuelRestMassData
import kotlin.math.min

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property fuelRestMass the rest mass of the fuel, can be converted to energy to change velocity
 * @property maxDeltaFuelRestMass maximum change of fuel mass per turn
 */
@Serializable
@SerialName("PhysicsData")
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val fuelRestMassData: FuelRestMassData = FuelRestMassData(),
    val targetFuelRestMassData: FuelRestMassData = FuelRestMassData(),
    val fuelRestMass: Double = 1.0,
    val maxDeltaFuelRestMass: Double = 0.0,
) : PlayerDataComponent() {
    fun totalRestMass() = coreRestMass + fuelRestMassData.total()
}

@Serializable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var targetFuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var fuelRestMass: Double = 1.0,
    var maxDeltaFuelRestMass: Double = 0.0,
) : MutablePlayerDataComponent() {
    fun totalRestMass() = coreRestMass + fuelRestMassData.total()
}