package relativitization.universe.game.mechanisms.defaults.regular.factory

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.physicsData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.mechanisms.Mechanism
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