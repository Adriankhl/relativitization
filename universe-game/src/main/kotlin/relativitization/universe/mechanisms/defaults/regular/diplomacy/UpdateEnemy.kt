package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.diplomacy.MutableRelationData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.mechanisms.Mechanism
import kotlin.random.Random

/**
 * Sync leader diplomatic relation and update relation based on war state
 */
object UpdateEnemy : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        val relationData: MutableRelationData = mutablePlayerData.playerInternalData.diplomacyData()
            .relationData

        val selfWarTargetId: Set<Int> = relationData.selfWarDataMap
            .flatMap { (opponentId, warData) ->
                computeOpponentAllySet(
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    supportId = warData.warCoreData.supportId,
                    opponentId = opponentId,
                ) + computeOpponentLeaderAllySet(
                    universeData3DAtPlayer = universeData3DAtPlayer,
                    supportId = warData.warCoreData.supportId,
                    opponentId = opponentId,
                    opponentLeaderIdList = warData.opponentLeaderIdList,
                ) + opponentId + warData.opponentLeaderIdList
            }.filter {
                !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
            }.toSet()

        val subordinateWarTargetId: Set<Int> = relationData.subordinateWarDataMap
            .flatMap { (_, warDataMap) ->
                warDataMap.flatMap { (opponentId, warData) ->
                    computeOpponentAllySet(
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        supportId = warData.warCoreData.supportId,
                        opponentId = opponentId,
                    ) + computeOpponentLeaderAllySet(
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        supportId = warData.warCoreData.supportId,
                        opponentId = opponentId,
                        opponentLeaderIdList = warData.opponentLeaderIdList,
                    ) + opponentId + warData.opponentLeaderIdList
                }
            }.filter {
                !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
            }.toSet()

        val allyWarTargetId: Set<Int> = relationData.allyWarDataMap
            .flatMap { (_, warDataMap) ->
                warDataMap.flatMap { (opponentId, warData) ->
                    computeOpponentAllySet(
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        supportId = warData.warCoreData.supportId,
                        opponentId = opponentId,
                    ) + computeOpponentLeaderAllySet(
                        universeData3DAtPlayer = universeData3DAtPlayer,
                        supportId = warData.warCoreData.supportId,
                        opponentId = opponentId,
                        opponentLeaderIdList = warData.opponentLeaderIdList,
                    ) + opponentId + warData.opponentLeaderIdList
                }
            }.filter {
                !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
            }.toSet()

        val allySubordinateWarTargetId: Set<Int> = relationData.allySubordinateWarDataMap
            .flatMap { (_, outerWarDataMap) ->
                outerWarDataMap.flatMap { (_, innerWarDataData) ->
                    innerWarDataData.flatMap { (opponentId, warData) ->
                        computeOpponentAllySet(
                            universeData3DAtPlayer = universeData3DAtPlayer,
                            supportId = warData.warCoreData.supportId,
                            opponentId = opponentId,
                        ) + computeOpponentLeaderAllySet(
                            universeData3DAtPlayer = universeData3DAtPlayer,
                            supportId = warData.warCoreData.supportId,
                            opponentId = opponentId,
                            opponentLeaderIdList = warData.opponentLeaderIdList,
                        ) + opponentId + warData.opponentLeaderIdList
                    }
                }
            }.filter {
                !mutablePlayerData.isLeaderOrSelf(it) && !mutablePlayerData.isSubOrdinate(it)
            }.toSet()

        val allWarTargetId: Set<Int> = selfWarTargetId + subordinateWarTargetId +
                allyWarTargetId + allySubordinateWarTargetId

        // All enemy from war, includes subordinate of war target
        val allWarEnemy: Set<Int> = allWarTargetId.flatMap {
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

    fun computeOpponentAllySet(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        supportId: Int,
        opponentId: Int,
    ): Set<Int> {
        return if (universeData3DAtPlayer.playerDataMap.containsKey(opponentId)) {
            universeData3DAtPlayer.get(opponentId).playerInternalData
                .diplomacyData().relationData.allyMap.keys.filter { opponentAllyId ->
                    if (universeData3DAtPlayer.playerDataMap.containsKey(opponentAllyId)) {
                        // whether this player has joined the war between opponentId and supportId
                        universeData3DAtPlayer.get(opponentAllyId).playerInternalData
                            .diplomacyData().relationData.hasAllyWar(
                                allyId = opponentId,
                                opponentId = supportId
                            )
                    } else {
                        false
                    }
                }.toSet()
        } else {
            setOf()
        }
    }

    fun computeOpponentLeaderAllySet(
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        supportId: Int,
        opponentId: Int,
        opponentLeaderIdList: List<Int>,
    ): Set<Int> {
        return opponentLeaderIdList.flatMap { opponentLeaderId ->
            if (universeData3DAtPlayer.playerDataMap.containsKey(opponentLeaderId)) {
                universeData3DAtPlayer.get(opponentLeaderId).playerInternalData.diplomacyData()
                    .relationData.allyMap.keys.filter { opponentLeaderAllyId ->
                        if (
                            universeData3DAtPlayer.playerDataMap.containsKey(opponentLeaderAllyId)
                        ) {
                            universeData3DAtPlayer.get(opponentLeaderAllyId).playerInternalData
                                .diplomacyData().relationData.hasAllySubordinateWar(
                                    allyId = opponentLeaderId,
                                    allySubordinateId = opponentId,
                                    opponentId = supportId,
                                )
                        } else {
                            false
                        }
                    }
            } else {
                listOf()
            }
        }.toSet()
    }
}