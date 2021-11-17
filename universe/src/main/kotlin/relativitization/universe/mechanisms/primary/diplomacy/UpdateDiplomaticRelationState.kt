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
 * Sync leader diplomatic relation and update relation based on war state
 */
object UpdateDiplomaticRelationState : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {

        // Determine top leader relation state by war
        // Only do the sync if the player is not a top leader
        if (mutablePlayerData.isTopLeader()) {
            val inWarSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.keys

            val toChangeSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.filter { (id, relation) ->
                    (relation.diplomaticRelationState == DiplomaticRelationState.ENEMY) && (!inWarSet.contains(
                        id
                    ))
                }.keys

            toChangeSet.forEach {
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.getValue(
                    it
                ).diplomaticRelationState = DiplomaticRelationState.NEUTRAL
            }
        } else {
            val directLeader: PlayerData = universeData3DAtPlayer.get(
                mutablePlayerData.playerInternalData.directLeaderId
            )


            val (prioritizeSelf, prioritizeDirectLeader) = mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys.partition {
                val topLeaderId: Int = universeData3DAtPlayer.get(it).topLeaderId()
                if (topLeaderId == mutablePlayerData.topLeaderId()) {
                    directLeader.playerInternalData.directSubordinateIdList.contains(it)
                } else {
                    true
                }
            }

            // Same direct leader or not the same top leader
            // the relation depends on their war status, follow the direct leader if no war
            prioritizeSelf.forEach { otherId ->
                // Allow internal war
                if (mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                        otherId
                    )
                ) {
                    mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                        otherId
                    ).diplomaticRelationState = DiplomaticRelationState.ENEMY
                } else {
                    val leaderRelationData: DiplomaticRelationData =
                        directLeader.playerInternalData.diplomacyData()
                            .getDiplomaticRelationData(otherId)

                    mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                        otherId
                    ).diplomaticRelationState = leaderRelationData.diplomaticRelationState
                }
            }

            prioritizeDirectLeader.forEach { otherId ->
                val leaderRelationData: DiplomaticRelationData =
                    directLeader.playerInternalData.diplomacyData()
                        .getDiplomaticRelationData(otherId)

                mutablePlayerData.playerInternalData.diplomacyData().getDiplomaticRelationData(
                    otherId
                ).diplomaticRelationState = leaderRelationData.diplomaticRelationState
            }
        }

        // Clear neutral and zero relation diplomatic relation
        mutablePlayerData.playerInternalData.diplomacyData().clearZeroRelationNeutral()

        return listOf()
    }
}