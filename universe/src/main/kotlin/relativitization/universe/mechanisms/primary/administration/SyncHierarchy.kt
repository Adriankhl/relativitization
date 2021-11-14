package relativitization.universe.mechanisms.primary.administration

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
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


        return if (mutablePlayerData.isTopLeader()) {
            listOf()
        } else {

            val directLeaderData: PlayerData = universeData3DAtPlayer.get(mutablePlayerData.playerInternalData.directLeaderId)

            mutablePlayerData.changeDirectLeaderId(directLeaderData.playerInternalData.leaderIdList)


            listOf()
        }
    }
}