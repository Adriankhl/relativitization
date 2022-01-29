package relativitization.universe.mechanisms.defaults.regular.sync

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

        // Add all leaders of direct leader
        if (!mutablePlayerData.isTopLeader()) {
            val leaderIdListOfLeader: List<Int> = universeData3DAtPlayer.get(
                mutablePlayerData.playerInternalData.directLeaderId
            ).playerInternalData.leaderIdList

            // In weird situation, it is possible that your leader is also viewing you as leader
            // the player should replace the leader list by one higher rank leader list
            // or become independent if it is empty
            val newLeaderList: List<Int> = leaderIdListOfLeader.takeWhile {
                !universeData3DAtPlayer.get(it).isLeader(mutablePlayerData.playerId)
            }

            mutablePlayerData.changeDirectLeaderId(newLeaderList)
        }

        // Send AddSubordinateCommand to direct leader if the leader data is not correct
        val toDirectLeaderCommandList: List<Command> = if (mutablePlayerData.isTopLeader()) {
            listOf()
        } else {

            val directLeaderData: PlayerData =
                universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            mutablePlayerData.changeDirectLeaderId(directLeaderData.playerInternalData.leaderIdList)

            if (directLeaderData.isDirectSubOrdinate(mutablePlayerData.playerId)) {
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
        val toRemoveDirectSubordinateIdList: List<Int> =
            mutablePlayerData.playerInternalData.directSubordinateIdList.filter {
                (universeData3DAtPlayer.get(it).playerInternalData.directLeaderId != mutablePlayerData.playerId) ||
                        mutablePlayerData.isLeaderOrSelf(it)
            }
        mutablePlayerData.playerInternalData.directSubordinateIdList.removeAll(
            toRemoveDirectSubordinateIdList
        )

        // Add all subordinates of direct subordinates
        mutablePlayerData.playerInternalData.directSubordinateIdList.map {
            universeData3DAtPlayer.get(it).playerInternalData.subordinateIdList
        }.flatten().filter {
            !mutablePlayerData.isLeaderOrSelf(it)
        }.forEach {
            mutablePlayerData.addSubordinateId(it)
        }

        // Clear subordinate
        val toRemoveSubordinateIdList: List<Int> =
            mutablePlayerData.playerInternalData.subordinateIdList.filter {
                !universeData3DAtPlayer.get(it).isLeaderOrSelf(mutablePlayerData.playerId)
            }
        mutablePlayerData.playerInternalData.subordinateIdList.removeAll(toRemoveSubordinateIdList)

        return toDirectLeaderCommandList
    }
}