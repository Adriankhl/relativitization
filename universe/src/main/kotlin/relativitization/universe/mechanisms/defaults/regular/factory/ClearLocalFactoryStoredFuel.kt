package relativitization.universe.mechanisms.defaults.regular.factory

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object ClearLocalFactoryStoredFuel : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Clear stored fuel in self factories and transfer to physics data
        mutablePlayerData.playerInternalData.popSystemData().carrierDataMap.values.forEach { carrier ->
            carrier.allPopData.labourerPopData.fuelFactoryMap.values.forEach { fuelFactory ->
                if (fuelFactory.ownerPlayerId == mutablePlayerData.playerId) {
                    val fuel: Double = fuelFactory.storedFuelRestMass
                    fuelFactory.storedFuelRestMass = 0.0
                    mutablePlayerData.playerInternalData.physicsData().addNewFuel(fuel)
                }
            }

            carrier.allPopData.labourerPopData.resourceFactoryMap.values.forEach { resourceFactory ->
                if (resourceFactory.ownerPlayerId == mutablePlayerData.playerId) {
                    val fuel: Double = resourceFactory.storedFuelRestMass
                    resourceFactory.storedFuelRestMass = 0.0
                    mutablePlayerData.playerInternalData.physicsData().addNewFuel(fuel)
                }
            }
        }

        return listOf()
    }
}