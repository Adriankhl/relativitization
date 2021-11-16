package relativitization.universe.mechanisms.primary.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Sync leader diplomatic relation
 */
object SyncDiplomaticRelation : Mechanism() {
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

            // Sync diplomatic relation if not belong to same direct leader
            // Allow internal war
            directLeader.playerInternalData.diplomacyData().relationMap.filterKeys {
                !directLeader.playerInternalData.directSubordinateIdList.contains(it)
            }.forEach { (playerId, relationData) ->
                mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                    playerId
                ).diplomaticRelationState = relationData.diplomaticRelationState
            }

        }

        return listOf()
    }
}