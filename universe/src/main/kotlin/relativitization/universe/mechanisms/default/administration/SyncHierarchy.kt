package relativitization.universe.mechanisms.default.administration

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AddDirectSubordinateCommand
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Sync leader and subordinate
 */
object SyncHierarchy : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Send AddSubordinateCommand to direct leader if the leader data is not correct
        val toDirectLeaderCommandList: List<Command> = if (mutablePlayerData.isTopLeader()) {
            listOf()
        } else {

            val directLeaderData: PlayerData = universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            mutablePlayerData.changeDirectLeaderId(directLeaderData.playerInternalData.leaderIdList)

            if (directLeaderData.playerInternalData.directSubordinateIdList.contains(mutablePlayerData.playerId)) {
                listOf()
            } else {
                listOf(
                    AddDirectSubordinateCommand(
                        toId = directLeaderData.playerId,
                        fromId = mutablePlayerData.playerId,
                        fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    )
                )
            }
        }

        // Clear direct subordinate
        val toRemoveDirectIdList: List<Int> = mutablePlayerData.playerInternalData.directSubordinateIdList.filter {
            universeData3DAtPlayer.get(it).playerInternalData.directLeaderId != mutablePlayerData.playerId
        }
        mutablePlayerData.playerInternalData.directSubordinateIdList.removeAll(toRemoveDirectIdList)

        // Clear subordinate
        val toRemoveIdList: List<Int> = mutablePlayerData.playerInternalData.directSubordinateIdList.filter {
            universeData3DAtPlayer.get(it).isLeaderOrSelf(mutablePlayerData.playerId)
        }
        mutablePlayerData.playerInternalData.subordinateIdList.removeAll(toRemoveIdList)


        // Add all subordinates of direct subordinates
        mutablePlayerData.playerInternalData.directSubordinateIdList.map {
            universeData3DAtPlayer.get(it).playerInternalData.subordinateIdList
        }.flatten().forEach {
            mutablePlayerData.addSubordinateId(it)
        }



        return toDirectLeaderCommandList
    }
}