package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.diplomacy.DiplomaticRelationState
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

            // Include subordinates of war target as enemy
            val allEnemy: Set<Int> = (inWarSet + inWarSet.map { inWarId ->
                // Enemy should not include self or subordinate
                universeData3DAtPlayer.get(inWarId).playerInternalData.subordinateIdList.filter {
                    !mutablePlayerData.isSubOrdinateOrSelf(it)
                }
            }.flatten()).toSet()

            val inRelationSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys

            // Change in war player to enemy
            allEnemy.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inRelationSet.filter {
                !allEnemy.contains(it) && (mutablePlayerData.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY)
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

            val allSelfEnemySet: Set<Int> = inSelfWarSet.map {
                universeData3DAtPlayer.get(it).playerInternalData.subordinateIdList
            }.flatten().toSet()

            val inSelfRelationSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys

            val inLeaderRelationSet: Set<Int> =
                directLeader.playerInternalData.diplomacyData().relationMap.keys

            // Sync leader enemy
            // Add self enemy
            val allEnemyList: List<Int> = inLeaderRelationSet.filter {
                directLeader.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            } + allSelfEnemySet
            allEnemyList.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inSelfRelationSet.filter {
                !allEnemyList.contains(it) && mutablePlayerData.playerInternalData.diplomacyData()
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