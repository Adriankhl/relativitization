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

        // In offensive war, the enemy leader also join the war
        val inSelfOffensiveWarEnemyTopLeaderSet: Set<Int> = inSelfOffensiveWarSet.map {
            universeData3DAtPlayer.get(it).topLeaderId()
        }.toSet()

        val inSelfDefensiveWarSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .warData.warStateMap.filter { !it.value.isOffensive }.keys

        val inSubordinateDefensiveWarSet: Set<Int> = mutablePlayerData.playerInternalData
            .subordinateIdSet.map { id ->
                universeData3DAtPlayer.get(id).playerInternalData.diplomacyData()
                    .warData.warStateMap.filter { !it.value.isOffensive }.keys
            }.flatten().toSet()

        val allWarSet: Set<Int> = (inSelfDefensiveWarSet + inSelfOffensiveWarEnemyTopLeaderSet +
                inSelfDefensiveWarSet + inSubordinateDefensiveWarSet).filter {
                    // Exclude subordinate and leader
                    !mutablePlayerData.isSubOrdinateOrSelf(it) &&
                            !mutablePlayerData.isLeader(it) &&
                            universeData3DAtPlayer.playerDataMap.containsKey(it)
        }.toSet()

        val allSelfEnemySet: Set<Int> = allWarSet.map {
            universeData3DAtPlayer.get(it).playerInternalData.subordinateIdSet + it
        }.flatten().filter {
            // Exclude subordinate and leader
            !mutablePlayerData.isSubOrdinateOrSelf(it) && !mutablePlayerData.isLeader(it)
        }.toSet()

        // If not a top leader, sync direct leader enemy
        val directLeaderEnemySet: Set<Int> = if (mutablePlayerData.isTopLeader()) {
            setOf()
        } else {
            val directLeader: PlayerData = universeData3DAtPlayer.get(
                mutablePlayerData.playerInternalData.directLeaderId
            )
            directLeader.playerInternalData.diplomacyData()
                .relationMap.filter { (_, relationData) ->
                    relationData.diplomaticRelationState == DiplomaticRelationState.ENEMY
                }.keys.filter {
                    // Exclude subordinate and leader
                    !mutablePlayerData.isSubOrdinateOrSelf(it) && !mutablePlayerData.isLeader(it)
                }.toSet()
        }

        val allEnemySet: Set<Int> = allSelfEnemySet + directLeaderEnemySet

        val inRelationSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .relationMap.keys

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

        // Clear neutral and zero relation diplomatic relation
        mutablePlayerData.playerInternalData.diplomacyData().clearZeroRelationNeutral()

        return listOf()
    }
}