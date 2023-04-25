package relativitization.universe.game.mechanisms.defaults.regular.politics

import relativitization.universe.game.data.MutablePlayerData
import relativitization.universe.game.data.PlayerData
import relativitization.universe.game.data.UniverseData3DAtPlayer
import relativitization.universe.game.data.UniverseSettings
import relativitization.universe.game.data.commands.Command
import relativitization.universe.game.data.components.MutablePoliticsData
import relativitization.universe.game.data.components.politicsData
import relativitization.universe.game.data.global.UniverseGlobalData
import relativitization.universe.game.data.serializer.DataSerializer
import relativitization.universe.game.mechanisms.Mechanism
import kotlin.random.Random

object UpdatePoliticsData : Mechanism() {
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

            val newPoliticsData: MutablePoliticsData =
                DataSerializer.copy(directLeader.playerInternalData.politicsData())

            mutablePlayerData.playerInternalData.politicsData(newPoliticsData)
        }

        return listOf()
    }

}