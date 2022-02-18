package relativitization.universe.data.components

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerInternalData
import relativitization.universe.data.PlayerInternalData
import relativitization.universe.data.components.defaults.physics.FuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.data.components.defaults.physics.FuelRestMassTargetProportionData
import kotlin.math.min

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property otherRestMass the other rest mass of the player, e.g., fuel stored in pop and factory
 * @property fuelRestMassData various data of fuel rest mass
 * @property fuelRestMassTargetProportionData target fuel rest mass proportion in the categories
 */
@Serializable
@SerialName("PhysicsData")
data class PhysicsData(
    val coreRestMass: Double = 1.0,
    val otherRestMass: Double = 0.0,
    val fuelRestMassData: FuelRestMassData = FuelRestMassData(),
    val fuelRestMassTargetProportionData: FuelRestMassTargetProportionData = FuelRestMassTargetProportionData(),
) : DefaultPlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()
}

@Serializable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var otherRestMass: Double = 0.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var fuelRestMassTargetProportionData: MutableFuelRestMassTargetProportionData =
        MutableFuelRestMassTargetProportionData(),
) : MutableDefaultPlayerDataComponent() {
    fun totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()

    /**
     * Add fuel such that it fulfill the target in the order of storage, movement, production, and
     * put the rest in trade, by recursion
     */
    fun addFuel(newFuelRestMass: Double) {
        val totalFuel: Double = newFuelRestMass + fuelRestMassData.total()
        val totalTargetWeight: Double = fuelRestMassTargetProportionData.total()

        val targetStorage: Double = if (totalTargetWeight > 0.0) {
            fuelRestMassTargetProportionData.storage / totalTargetWeight * totalFuel
        } else {
            totalFuel * 0.25
        }
        val targetMovement: Double = if (totalTargetWeight > 0.0) {
            fuelRestMassTargetProportionData.movement / totalTargetWeight * totalFuel
        } else {
            totalFuel * 0.25
        }
        val targetProduction: Double = if (totalTargetWeight > 0.0) {
            fuelRestMassTargetProportionData.production / totalTargetWeight * totalFuel
        } else {
            totalFuel * 0.25
        }

        if (newFuelRestMass > 0.0) {
            when {
                fuelRestMassData.storage < targetStorage -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetStorage - fuelRestMassData.storage
                    )
                    fuelRestMassData.storage += actualFuelAdded
                    addFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.movement < targetMovement -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetMovement - fuelRestMassData.movement
                    )
                    fuelRestMassData.movement += actualFuelAdded
                    addFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.production < targetProduction -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetProduction - fuelRestMassData.production
                    )
                    fuelRestMassData.production += actualFuelAdded
                    addFuel(newFuelRestMass - actualFuelAdded)
                }
                else -> {
                    fuelRestMassData.trade += newFuelRestMass
                }
            }
        }
    }
}

fun PlayerInternalData.physicsData(): PhysicsData =
    playerDataComponentMap.getOrDefault(PhysicsData::class, PhysicsData())

fun MutablePlayerInternalData.physicsData(): MutablePhysicsData =
    playerDataComponentMap.getOrDefault(MutablePhysicsData::class, MutablePhysicsData())

fun MutablePlayerInternalData.physicsData(newPhysicsData: MutablePhysicsData) =
    playerDataComponentMap.put(newPhysicsData)


