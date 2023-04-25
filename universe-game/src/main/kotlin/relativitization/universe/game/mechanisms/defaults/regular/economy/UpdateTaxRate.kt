package relativitization.universe.game.mechanisms.defaults.regular.economy

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.PlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.economyData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object UpdateTaxRate : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
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