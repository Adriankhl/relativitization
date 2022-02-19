package relativitization.universe.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.MutablePlayerInternalData

@Serializable
sealed class DefaultPlayerDataComponent : PlayerDataComponent()

@Serializable
sealed class MutableDefaultPlayerDataComponent : MutablePlayerDataComponent()

/**
 * Synchronize different data component to ensure consistency
 */
fun MutablePlayerData.syncData() {
    playerInternalData.syncDataComponent()

    // Add mass from new player to other rest mass
    playerInternalData.physicsData().otherRestMass += newPlayerList.sumOf {
        it.physicsData().totalRestMass()
    }
}

/**
 * Synchronize different data component to ensure consistency
 */
fun MutablePlayerInternalData.syncDataComponent() {
    physicsData().coreRestMass =
        popSystemData().totalCoreRestMass()

    physicsData().otherRestMass =
        popSystemData().totalOtherRestMass()

    physicsData().fuelRestMassData.maxMovementDelta =
        popSystemData().totalMaxMovementDeltaFuelRestMass()
}
