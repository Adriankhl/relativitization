package relativitization.universe.mechanisms.defaults.regular.politics

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.commands.MergeCarrierCommand
import relativitization.universe.data.components.politicsData
import relativitization.universe.data.components.popSystemData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.data.serializer.DataSerializer
import relativitization.universe.mechanisms.Mechanism

object MergePlayer : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Change agree merge state to false if the player is a top leader
        if (mutablePlayerData.isTopLeader()) {
            mutablePlayerData.playerInternalData.politicsData().agreeMerge = false
        }

        return if ((mutablePlayerData.playerInternalData.politicsData().agreeMerge) &&
            (!mutablePlayerData.isTopLeader())
        ) {

            val directLeader: PlayerData =
                universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            if ((directLeader.int4D == mutablePlayerData.int4D.toInt4D()) &&
                (directLeader.groupId == mutablePlayerData.groupId)
            ) {
                val mergeCarrierCommand: Command = MergeCarrierCommand(
                    toId = directLeader.playerId,
                    fromId = mutablePlayerData.playerId,
                    fromInt4D = mutablePlayerData.int4D.toInt4D(),
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