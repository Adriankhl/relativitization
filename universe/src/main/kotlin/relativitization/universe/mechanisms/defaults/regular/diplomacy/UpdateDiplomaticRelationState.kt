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

        val inSelfOffensiveWarSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .warData.warStateMap.filter { it.value.isOffensive }.keys

        val inSelfDefensiveWarSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .warData.warStateMap.filter { !it.value.isOffensive }.keys

        val inSubordinateDefensiveWarSet: Set<Int> = mutablePlayerData.playerInternalData
            .subordinateIdList.map { id ->
                universeData3DAtPlayer.get(id).playerInternalData.diplomacyData()
                    .warData.warStateMap.filter { !it.value.isOffensive }.keys
            }.flatten().toSet()

        // Treat top leader and subordinate differently
        if (mutablePlayerData.isTopLeader()) {
            val inWarSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.keys

            val inSubordinateWarSet: Set<Int> = mutablePlayerData.playerInternalData
                .subordinateIdList.map { id ->
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData()
                        .warData.warStateMap.keys
                }.flatten().toSet()

            val allWarSet: Set<Int> = (inWarSet + inSubordinateWarSet).filter {
                // Exclude subordinate
                !mutablePlayerData.isSubOrdinateOrSelf(it)
            }.toSet()

            val allWarTopLeaderSet: Set<Int> = allWarSet.map {
                universeData3DAtPlayer.get(it).topLeaderId()
            }.toSet()

            // Compute the enemy by getting all the subordinates of the war target and their top leader
            val allEnemySet: Set<Int> = (allWarSet + allWarTopLeaderSet).map {
                universeData3DAtPlayer.get(it).playerInternalData.subordinateIdList + it
            }.flatten().filter {
                // Exclude subordinate
                !mutablePlayerData.isSubOrdinateOrSelf(it)
            }.toSet()

            val inRelationSet: Set<Int> =
                mutablePlayerData.playerInternalData.diplomacyData().relationMap.keys

            // Change in war player to enemy
            allEnemySet.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inRelationSet.filter {
                !allEnemySet.contains(it) && (mutablePlayerData.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY)
            }.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.NEUTRAL
            }
        } else {
            // Sync direct leader enemy if the player is not a top leader
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
            val allEnemySet: Set<Int> = (inLeaderRelationSet.filter {
                directLeader.playerInternalData.diplomacyData()
                    .getRelationState(it) == DiplomaticRelationState.ENEMY
            }.toSet() + allSelfEnemySet).filter {
                // Exclude subordinate
                !mutablePlayerData.isSubOrdinateOrSelf(it)
            }.toSet()

            allEnemySet.forEach {
                mutablePlayerData.playerInternalData.diplomacyData()
                    .getDiplomaticRelationData(it).diplomaticRelationState =
                    DiplomaticRelationState.ENEMY
            }

            // Change not in war player from enemy to neutral
            inSelfRelationSet.filter {
                !allEnemySet.contains(it) && mutablePlayerData.playerInternalData.diplomacyData()
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