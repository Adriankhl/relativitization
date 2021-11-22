package relativitization.universe.mechanisms.default.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.default.diplomacy.DiplomaticRelationData
import relativitization.universe.data.components.default.diplomacy.DiplomaticRelationState
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

            val inRelationSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys

            // Change in war player to enemy
            inWarSet.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inRelationSet.filter {
                !inWarSet.contains(it) && mutablePlayerData.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            }.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.NEUTRAL
            }
        } else {
            val directLeader: PlayerData = universeData3DAtPlayer.get(
                mutablePlayerData.playerInternalData.directLeaderId
            )

            val inSelfWarSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.keys

            val inSelfRelationSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys

            val inLeaderRelationSet: Set<Int> =
                directLeader.playerInternalData.diplomacyData().relationMap.keys

            // Sync enemy
            inLeaderRelationSet.filter {
                directLeader.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            }.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }



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