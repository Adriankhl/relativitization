package relativitization.universe.game.data.components

import kotlinx.serialization.SerialName
import ksergen.annotations.GenerateImmutable
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.PlayerInternalData
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassData
import relativitization.universe.game.data.components.defaults.physics.MutableFuelRestMassTargetProportionData
import relativitization.universe.game.data.components.defaults.physics.MutableTargetDouble3DData
import relativitization.universe.game.data.components.defaults.physics.total
import kotlin.math.min

/**
 * Player data related to physics
 *
 * @property coreRestMass the core rest mass of the player, cannot be converted to energy
 * @property otherRestMass the other rest mass of the player, e.g., fuel stored in pop and factory
 * @property fuelRestMassData various data of fuel rest mass
 * @property fuelRestMassTargetProportionData target fuel rest mass proportion in the categories
 * @property targetDouble3DData the target position to move to
 */
@GenerateImmutable
@SerialName("PhysicsData")
data class MutablePhysicsData(
    var coreRestMass: Double = 1.0,
    var otherRestMass: Double = 0.0,
    var fuelRestMassData: MutableFuelRestMassData = MutableFuelRestMassData(),
    var fuelRestMassTargetProportionData: MutableFuelRestMassTargetProportionData =
        MutableFuelRestMassTargetProportionData(),
    var targetDouble3DData: MutableTargetDouble3DData = MutableTargetDouble3DData(),
) : MutableDefaultPlayerDataComponent() {
    /**
     * Add internal fuel from other part of the player,
     * such that it fulfill the target in the order of storage, movement, production, and
     * put the rest in trade, by recursion
     *
     * @param newFuelRestMass the rest mass of the fuel to be added
     */
    fun addInternalFuel(newFuelRestMass: Double) {
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
                    addInternalFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.movement < targetMovement -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetMovement - fuelRestMassData.movement
                    )
                    fuelRestMassData.movement += actualFuelAdded
                    addInternalFuel(newFuelRestMass - actualFuelAdded)
                }
                fuelRestMassData.production < targetProduction -> {
                    val actualFuelAdded: Double = min(
                        newFuelRestMass,
                        targetProduction - fuelRestMassData.production
                    )
                    fuelRestMassData.production += actualFuelAdded
                    addInternalFuel(newFuelRestMass - actualFuelAdded)
                }
                else -> {
                    fuelRestMassData.trade += newFuelRestMass
                }
            }
        }
    }

    /**
     * Add external fuel from other player,
     * such that it fulfill the target in the order of storage, movement, production, and
     * put the rest in trade, by recursion. And increase otherRestMass
     *
     * @param newFuelRestMass the rest mass of the fuel to be added
     */
    fun addExternalFuel(newFuelRestMass: Double) {
        otherRestMass += newFuelRestMass
        addInternalFuel(newFuelRestMass)
    }

    /**
     *  Remove fuel from storage
     */
    fun removeInternalStorageFuel(removeFuelRestMass: Double) {
        fuelRestMassData.storage -= removeFuelRestMass
    }

    /**
     *  Remove fuel from storage and decrease otherRestMass
     */
    fun removeExternalStorageFuel(removeFuelRestMass: Double) {
        otherRestMass -= removeFuelRestMass
        removeInternalStorageFuel(removeFuelRestMass)
    }

    /**
     *  Remove fuel from movement
     */
    fun removeInternalMovementFuel(removeFuelRestMass: Double) {
        fuelRestMassData.movement -= removeFuelRestMass
    }

    /**
     *  Remove fuel from movement and decrease otherRestMass
     */
    fun removeExternalMovementFuel(removeFuelRestMass: Double) {
        otherRestMass -= removeFuelRestMass
        removeInternalMovementFuel(removeFuelRestMass)
    }

    /**
     *  Remove fuel from production
     */
    fun removeInternalProductionFuel(removeFuelRestMass: Double) {
        fuelRestMassData.production -= removeFuelRestMass
    }

    /**
     *  Remove fuel from production and decrease otherRestMass
     */
    fun removeExternalProductionFuel(removeFuelRestMass: Double) {
        otherRestMass -= removeFuelRestMass
        removeInternalProductionFuel(removeFuelRestMass)
    }

    /**
     *  Remove fuel from trade
     */
    fun removeInternalTradeFuel(removeFuelRestMass: Double) {
        fuelRestMassData.trade -= removeFuelRestMass
    }

    /**
     *  Remove fuel from trade and decrease otherRestMass
     */
    fun removeExternalTradeFuel(removeFuelRestMass: Double) {
        otherRestMass -= removeFuelRestMass
        removeInternalTradeFuel(removeFuelRestMass)
    }
}

fun PhysicsData.totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()

fun MutablePhysicsData.totalRestMass() = coreRestMass + otherRestMass + fuelRestMassData.total()

fun PlayerInternalData.physicsData(): PhysicsData =
    playerDataComponentMap.get()

fun MutablePlayerInternalData.physicsData(): MutablePhysicsData =
    playerDataComponentMap.get()


fun MutablePlayerInternalData.physicsData(newPhysicsData: MutablePhysicsData) =
    playerDataComponentMap.put(newPhysicsData)