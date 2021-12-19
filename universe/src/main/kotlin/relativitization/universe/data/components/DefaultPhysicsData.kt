package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import kotlin.math.min

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
) : DefaultPlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()
}

@Serializable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var otherRestMass: Double = 0.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var targetFuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
) : MutableDefaultPlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()

    /**
     * Add fuel such that it fulfill the target in the order of storage, movement, production, and
     * put the rest in trade, by recursion
     */
    fun addNewFuel(newFuelRestMass: Double) {
        if (newFuelRestMass > 0.0) {
            when {
                fuelRestMassData.storage < targetFuelRestMassData.storage -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetFuelRestMassData.storage - fuelRestMassData.storage
                    )
                    fuelRestMassData.storage += actualFuelAdded
                    addNewFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.movement < targetFuelRestMassData.movement -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetFuelRestMassData.movement - fuelRestMassData.movement
                    )
                    fuelRestMassData.movement += actualFuelAdded
                    addNewFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.production < targetFuelRestMassData.production -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetFuelRestMassData.movement - fuelRestMassData.movement
                    )
                    fuelRestMassData.production += actualFuelAdded
                    addNewFuel(newFuelRestMass - actualFuelAdded)
                }
                else -> {
                    fuelRestMassData.trade += newFuelRestMass
                }
            }
        }
    }
}