package relativitization.universe.mechanisms.defaults.regular.economy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.mechanisms.Mechanism

object UpdateTaxRate : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Only do the sync if the player is not a top leader
        if (!mutablePlayerData.isTopLeader()) {
            val directLeader: PlayerData = universeData3DAtPlayer.get(
                mutablePlayerData.playerInternalData.directLeaderId
            )

            mutablePlayerData.playerInternalData.economyData().taxData.taxRateData =
                DataSerializer.copy(directLeader.playerInternalData.economyData().taxData.taxRateData)

        }

        return listOf()
    }
}