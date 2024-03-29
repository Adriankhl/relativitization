package relativitization.universe.game.data.components

import kotlinx.serialization.Serializable
import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.MutablePlayerInternalData
import relativitization.universe.core.data.components.MutablePlayerDataComponent
import relativitization.universe.core.data.components.PlayerDataComponent
import relativitization.universe.core.data.serializer.DataSerializer

@Serializable
sealed class DefaultPlayerDataComponent : PlayerDataComponent() {
    companion object {
        fun createComponentList(): List<DefaultPlayerDataComponent> {
            return DataSerializer.copy(
                MutableDefaultPlayerDataComponent.createComponentList()
            )
        }
    }
}

@Serializable
sealed class MutableDefaultPlayerDataComponent : MutablePlayerDataComponent() {
    companion object {
        fun createComponentList(): List<MutableDefaultPlayerDataComponent> {
            return listOf(
                MutableAIData(),
                MutableDiplomacyData(),
                MutableEconomyData(),
                MutableModifierData(),
                MutablePhysicsData(),
                MutablePlayerScienceData(),
                MutablePoliticsData(),
                MutablePopSystemData(),
            )
        }
    }
}

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
