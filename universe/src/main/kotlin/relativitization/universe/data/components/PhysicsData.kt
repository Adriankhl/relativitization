package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.default.physics.FuelRestMassData
import relativitization.universe.data.components.default.physics.MutableFuelRestMassData

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property otherRestMass the other rest mass of the player, e.g., fuel stored in pop and factory
 * @property fuelRestMassData various data of fuel rest mass
 * @property targetFuelRestMassData target fuel rest mass storage
 */
@Serializable
@SerialName("PhysicsData")
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val otherRestMass: Double = 0.0,
    val fuelRestMassData: FuelRestMassData = FuelRestMassData(),
    val targetFuelRestMassData: FuelRestMassData = FuelRestMassData(),
) : PlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()
}

@Serializable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var otherRestMass: Double = 0.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var targetFuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
) : MutablePlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()

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