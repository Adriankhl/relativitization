package relativitization.universe.mechanisms.default.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
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

            // Sync leader enemy
            // Add self enemy
            val allWarList: List<Int> = inLeaderRelationSet.filter {
                directLeader.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            } + inSelfWarSet
            allWarList.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inSelfRelationSet.filter {
                !allWarList.contains(it) && mutablePlayerData.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            }.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.NEUTRAL
            }
        }

        // Clear neutral and zero relation diplomatic relation
        mutablePlayerData.playerInternalData.diplomacyData().clearZeroRelationNeutral()

        return listOf()
    }
}