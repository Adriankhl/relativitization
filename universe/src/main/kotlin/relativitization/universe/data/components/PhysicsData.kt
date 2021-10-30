package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.physics.FuelRestMassData
import relativitization.universe.data.components.physics.MutableFuelRestMassData

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property fuelRestMassData various data of fuel rest mass
 * @property targetFuelRestMassData target fuel rest mass storage
 */
@Serializable
@SerialName("PhysicsData")
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val fuelRestMassData: FuelRestMassData = FuelRestMassData(),
    val targetFuelRestMassData: FuelRestMassData = FuelRestMassData(),
) : PlayerDataComponent() {
    fun totalRestMass() = coreRestMass + fuelRestMassData.total()
}

@Serializable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var targetFuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
) : MutablePlayerDataComponent() {
    fun totalRestMass() = coreRestMass + fuelRestMassData.total()

    fun addFuel(restMass: Double) {
        when {
            fuelRestMassData.movement <= targetFuelRestMassData.movement -> {
                fuelRestMassData.movement += restMass
            }
            fuelRestMassData.production <= targetFuelRestMassData.production -> {
                fuelRestMassData.production += restMass
            }
            else -> {
                fuelRestMassData.trade += restMass
            }
        }
    }
}