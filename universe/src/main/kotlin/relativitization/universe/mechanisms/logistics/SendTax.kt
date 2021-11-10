package relativitization.universe.mechanisms.logistics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

object SendTax : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        val fraction: Double = 0.5

        val fuelRestMass: Double = mutablePlayerData.playerInternalData.economyData().taxData.storedFuelRestMass

        val leaderList: List<Int> = mutablePlayerData.playerInternalData.leaderIdList



        return listOf()
    }
}