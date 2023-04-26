package relativitization.universe.game.mechanisms.defaults.regular.factory

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

object ClearLocalFactoryStoredFuel : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // Clear stored fuel in self factories and transfer to physics data
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.fuelFactoryMap.values.forEach { fuelFactory ->
                if (fuelFactory.ownerPlayerId == mutablePlayerData.playerId) {
                    val fuel: Double = fuelFactory.storedFuelRestMass
                    fuelFactory.storedFuelRestMass = 0.0
                    mutablePlayerData.playerInternalData.physicsData().addInternalFuel(fuel)
                }
            }

            carrier.allPopData.labourerPopData.resourceFactoryMap.values.forEach { resourceFactory ->
                if (resourceFactory.ownerPlayerId == mutablePlayerData.playerId) {
                    val fuel: Double = resourceFactory.storedFuelRestMass
                    resourceFactory.storedFuelRestMass = 0.0
                    mutablePlayerData.playerInternalData.physicsData().addInternalFuel(fuel)
                }
            }
        }

        return listOf()
    }
}