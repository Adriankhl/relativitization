package relativitization.universe.game.mechanisms.defaults.regular.politics

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.game.data.commands.MergeCarrierCommand
import relativitization.universe.game.data.components.politicsData
import relativitization.universe.game.data.components.popSystemData
import kotlin.random.Random

object MergePlayer : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {

        // Change agree merge state to false if the player is a top leader
        if (mutablePlayerData.isTopLeader()) {
            mutablePlayerData.playerInternalData.politicsData().hasAgreedMerge = false
        }

        return if ((mutablePlayerData.playerInternalData.politicsData().hasAgreedMerge) &&
            (!mutablePlayerData.isTopLeader())
        ) {

            val directLeader: PlayerData =
                universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            if ((directLeader.int4D == mutablePlayerData.int4D.toInt4D()) &&
                (directLeader.groupId == mutablePlayerData.groupId)
            ) {
                val mergeCarrierCommand: Command = MergeCarrierCommand(
                    toId = directLeader.playerId,
                    carrierList = DataSerializer.copy(mutablePlayerData.playerInternalData.popSystemData()
                        .carrierDataMap.values.toList())
                )

                // This player is dead
                mutablePlayerData.playerInternalData.isAlive = false

                listOf(mergeCarrierCommand)
            } else {
                listOf()
            }
        } else {
            listOf()
        }
    }
}