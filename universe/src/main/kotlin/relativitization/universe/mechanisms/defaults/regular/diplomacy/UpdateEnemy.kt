package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism

/**
 * Sync leader diplomatic relation and update relation based on war state
 */
object UpdateEnemy : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        val allWarTargetId: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .relationData.allWarTargetId()

        val allOffensiveWarTargetId: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .relationData.allOffensiveWarTargetId()

        // Leaders of opponent in offensive war are enemy
        val allOffensiveWarTargetLeaderId: Set<Int> = allOffensiveWarTargetId.flatMap {
            if (universeData3DAtPlayer.playerDataMap.containsKey(it)) {
                universeData3DAtPlayer.get(it).playerInternalData.leaderIdList
            } else {
                listOf()
            }
        }.filter {
            !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
        }.toSet()

        // All enemy from war, includes subordinate of war target
        val allWarEnemy: Set<Int> = (allWarTargetId + allOffensiveWarTargetLeaderId).flatMap {
            if (universeData3DAtPlayer.playerDataMap.containsKey(it)) {
                universeData3DAtPlayer.get(it).getSubordinateAndSelfIdSet()
            } else {
                setOf(it)
            }
        }.filter {
            !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
        }.toSet()

        // Also include enemy from direct leader
        val allDirectLeaderEnemy: Set<Int> = if (mutablePlayerData.isTopLeader()) {
            setOf()
        } else {
            if (universeData3DAtPlayer.playerDataMap.containsKey(mutablePlayerData.topLeaderId())) {
                universeData3DAtPlayer.get(mutablePlayerData.topLeaderId()).playerInternalData
                    .diplomacyData().relationData.enemyIdSet
            } else {
                setOf()
            }
        }

        // Update enemy Id set
        mutablePlayerData.playerInternalData.diplomacyData().relationData.enemyIdSet.clear()
        mutablePlayerData.playerInternalData.diplomacyData().relationData.enemyIdSet.addAll(
            allWarEnemy + allDirectLeaderEnemy
        )

        return listOf()
    }
}