package relativitization.universe.mechanisms.primary.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.diplomacy.DiplomaticRelationState
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

            val (sameDirectLeader, otherDirectLeader) = mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys.partition {
                directLeader.playerInternalData.directSubordinateIdList.contains(it)
            }

            // Sync diplomatic relation if not belong to same direct leader
            // Allow internal war
            otherDirectLeader.forEach { otherId ->
                val leaderRelationData: DiplomaticRelationData = directLeader.playerInternalData.diplomacyData().getDiplomaticRelationData(otherId)

                mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                    otherId
                ).diplomaticRelationState = leaderRelationData.diplomaticRelationState
            }

            // Same direct leader, the relation depends on their war status, follow the direct leader if no war
            sameDirectLeader.forEach { otherId ->
                if (mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(otherId)) {
                    mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                        otherId
                    ).diplomaticRelationState = DiplomaticRelationState.ENEMY
                } else {
                    val leaderRelationData: DiplomaticRelationData = directLeader.playerInternalData.diplomacyData().getDiplomaticRelationData(otherId)

                    mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                        otherId
                    ).diplomaticRelationState = leaderRelationData.diplomaticRelationState
                }
            }
        }

        return listOf()
    }
}