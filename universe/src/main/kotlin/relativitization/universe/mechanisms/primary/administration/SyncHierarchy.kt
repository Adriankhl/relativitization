package relativitization.universe.mechanisms.primary.administration

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.AddSubordinateCommand
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

        val toDirectLeaderCommandList: List<Command> = if (mutablePlayerData.isTopLeader()) {
            listOf()
        } else {

            val directLeaderData: PlayerData = universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            mutablePlayerData.changeDirectLeaderId(directLeaderData.playerInternalData.leaderIdList)

            if (directLeaderData.playerInternalData.directSubordinateIdList.contains(mutablePlayerData.playerId)) {
                listOf()
            } else {
                listOf(
                    AddSubordinateCommand(
                        toId = directLeaderData.playerId,
                        fromId = mutablePlayerData.playerId,
                        fromInt4D = mutablePlayerData.int4D.toInt4D(),
                    )
                )
            }
        }

        return toDirectLeaderCommandList
    }
}