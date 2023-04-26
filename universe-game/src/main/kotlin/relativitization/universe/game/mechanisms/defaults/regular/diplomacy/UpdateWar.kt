package relativitization.universe.game.mechanisms.defaults.regular.diplomacy

import relativitization.universe.core.data.MutablePlayerData
import relativitization.universe.core.data.PlayerData
import relativitization.universe.core.data.UniverseData3DAtPlayer
import relativitization.universe.core.data.UniverseSettings
import relativitization.universe.core.data.commands.Command
import relativitization.universe.game.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.game.data.components.defaults.diplomacy.war.WarData
import relativitization.universe.game.data.components.diplomacyData
import relativitization.universe.game.data.components.modifierData
import relativitization.universe.game.data.components.popSystemData
import relativitization.universe.core.data.global.UniverseGlobalData
import relativitization.universe.core.data.serializer.DataSerializer
import relativitization.universe.core.maths.physics.Intervals
import relativitization.universe.core.mechanisms.Mechanism
import relativitization.universe.core.utils.RelativitizationLogManager
import relativitization.universe.game.data.components.defaults.diplomacy.hasSubordinateWar
import relativitization.universe.game.data.components.defaults.diplomacy.isAlly
import relativitization.universe.game.data.components.totalAdultPopulation
import kotlin.random.Random

object UpdateWar : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    // Parameters
    private const val peaceTreatyLength: Int = 15
    private const val maxWarLength: Int = 100

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData,
        random: Random
    ): List<Command> {
        initializeWar(mutablePlayerData, universeData3DAtPlayer)
        updateSelfWar(mutablePlayerData, universeData3DAtPlayer, universeSettings)
        updateSubordinateWar(mutablePlayerData, universeData3DAtPlayer)
        updateAllyWar(mutablePlayerData, universeData3DAtPlayer)
        updateAllySubordinateWar(mutablePlayerData, universeData3DAtPlayer)

        return listOf()
    }

    private fun initializeWar(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        val selfTotalPopulation: Double = mutablePlayerData.playerInternalData.popSystemData()
            .totalAdultPopulation()

        val subordinateTotalPopulation: Double = mutablePlayerData.playerInternalData
            .subordinateIdSet.sumOf {
                val hasPlayer: Boolean = universeData3DAtPlayer.playerDataMap.containsKey(it)
                if (hasPlayer) {
                    universeData3DAtPlayer.get(it).playerInternalData.popSystemData()
                        .totalAdultPopulation()
                } else {
                    0.0
                }
            }

        val totalPopulation: Double = selfTotalPopulation + subordinateTotalPopulation

        mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap.values
            .forEach {
                it.initializePopulation(totalPopulation)
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.subordinateWarDataMap
            .forEach { (_, warDataMap) ->
                warDataMap.forEach { (_, warData) ->
                    warData.initializePopulation(totalPopulation)
                }
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyWarDataMap
            .forEach { (_, warDataMap) ->
                warDataMap.forEach { (_, warData) ->
                    warData.initializePopulation(totalPopulation)
                }
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allySubordinateWarDataMap
            .forEach { (_, outerWarDataMap) ->
                outerWarDataMap.forEach { (_, innerWarDataMap) ->
                    innerWarDataMap.forEach { (_, warData) ->
                        warData.initializePopulation(totalPopulation)
                    }
                }
            }
    }

    private fun updateSelfWar(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
    ) {
        // Clear weird war targeting self
        val hasWeirdSelfWar: Boolean = mutablePlayerData.playerInternalData.diplomacyData()
            .relationData.selfWarDataMap.containsKey(mutablePlayerData.playerId)
        if (hasWeirdSelfWar) {
            logger.error("Player ${mutablePlayerData.playerId} is in war with self")
            mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .remove(mutablePlayerData.playerId)
        }

        // Clear self war with incorrect id
        mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
            .entries.removeAll { (opponentId, warData) ->
                val isSelfIdCorrect: Boolean =
                    warData.warCoreData.supportId == mutablePlayerData.playerId
                if (!isSelfIdCorrect) {
                    logger.error("Self war incorrect supportId")
                }

                val isOpponentIdCorrect: Boolean = warData.warCoreData.opponentId == opponentId
                if (!isOpponentIdCorrect) {
                    logger.error("Self war incorrect opponentId")
                }

                !isSelfIdCorrect || !isOpponentIdCorrect
            }

        // Clear war with player that should be at peace
        mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap.keys
            .removeAll {
                mutablePlayerData.playerInternalData.diplomacyData().peacePlayerIdSet.contains(it)
            }

        // Invalid internal war, is leader or subordinate
        val leaderOrSubordinateWarSet: Set<Int> = mutablePlayerData.playerInternalData
            .diplomacyData().relationData.selfWarDataMap.filter { (id, _) ->
                mutablePlayerData.isLeader(id) || mutablePlayerData.isSubOrdinate(id)
            }.keys

        // Remove the war if the player does not exist, i.e., dead
        val playerNotExistSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData()
            .relationData.selfWarDataMap.filter { (id, _) ->
                !universeData3DAtPlayer.playerDataMap.containsKey(id)
            }.keys

        val validWarMap: Map<Int, MutableWarData> = mutablePlayerData.playerInternalData
            .diplomacyData().relationData.selfWarDataMap.filterKeys { id ->
                !leaderOrSubordinateWarSet.contains(id) && !playerNotExistSet.contains(id)
            }

        // Force the war to stop if the length is too long
        val warTooLongSet: Set<Int> = validWarMap.filter { (_, warData) ->
            (mutablePlayerData.int4D.t - warData.warCoreData.startTime > maxWarLength)
        }.keys

        // Filter out those who should have received the war declaration
        // and the information should have traveled back
        val warOldEnoughSet: Set<Int> = validWarMap.filter { (id, warData) ->
            val timeDelay: Int = Intervals.intDelay(
                universeData3DAtPlayer.get(id).int4D.toInt3D(),
                mutablePlayerData.int4D.toInt3D(),
                universeSettings.speedOfLight
            )
            val timeDiff: Int = mutablePlayerData.int4D.t - warData.warCoreData.startTime

            // Offensive war need to wait the information to travel back, so 2 times the delay
            if (warData.warCoreData.isOffensive) {
                timeDiff >= 2 * timeDelay
            } else {
                timeDiff >= timeDelay
            }
        }.keys

        // Filter out which other war state has disappeared
        val opponentNoWarStateSet: Set<Int> = warOldEnoughSet.filter { id ->
            !universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().relationData
                .selfWarDataMap.containsKey(mutablePlayerData.playerId)
        }.toSet()

        // All player to get peace treaty
        val allPeaceSet: Set<Int> = leaderOrSubordinateWarSet +
                playerNotExistSet +
                warTooLongSet +
                opponentNoWarStateSet

        allPeaceSet.forEach { otherPlayerId ->
            // If this is an offensive war, add the enemy top leader and subordinate to peace treaty
            val warData: MutableWarData = mutablePlayerData.playerInternalData.diplomacyData()
                .relationData.selfWarDataMap.getValue(otherPlayerId)
            val isOffensiveWar: Boolean = warData.warCoreData.isOffensive

            val peaceTreatyIdSet: Set<Int> = if (isOffensiveWar) {
                // Peace treaty with opponent's leader if this is an offensive war
                warData.opponentLeaderIdList.filter {
                    !mutablePlayerData.isLeader(it) && !mutablePlayerData.isSubOrdinate(it)
                }.toSet() + otherPlayerId
            } else {
                setOf(otherPlayerId)
            }

            peaceTreatyIdSet.forEach {
                mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData
                    .setPeaceTreatyWithLength(
                        it,
                        peaceTreatyLength
                    )
            }

            mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
                .remove(otherPlayerId)
        }

        // Update war leader only for offensive war
        mutablePlayerData.playerInternalData.diplomacyData().relationData.selfWarDataMap
            .forEach { (otherPlayerId, warData) ->
                warData.opponentLeaderIdList.clear()

                if (warData.warCoreData.isOffensive) {
                    warData.opponentLeaderIdList.addAll(
                        universeData3DAtPlayer.get(otherPlayerId).playerInternalData.leaderIdList
                            .filter {
                                !mutablePlayerData.isLeaderOrSelf(it) &&
                                        !mutablePlayerData.isSubOrdinate(it)
                            }
                    )
                }
            }
    }

    private fun updateSubordinateWar(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer
    ) {
        // Remove invalid subordinate war
        mutablePlayerData.playerInternalData.diplomacyData().relationData.subordinateWarDataMap
            .forEach { (supportId, warDataMap) ->
                warDataMap.entries.removeAll { (opponentId, warData) ->
                    val hasPlayer: Boolean = universeData3DAtPlayer.playerDataMap
                        .containsKey(warData.warCoreData.supportId)

                    if (hasPlayer) {
                        val subordinatePlayerData: PlayerData = universeData3DAtPlayer
                            .get(warData.warCoreData.supportId)

                        val isSupportIdCorrect: Boolean =
                            supportId == warData.warCoreData.supportId
                        val isOpponentIdCorrect: Boolean =
                            opponentId == warData.warCoreData.opponentId

                        val isSubordinate: Boolean =
                            mutablePlayerData.isSubOrdinate(warData.warCoreData.supportId)

                        val isWarExist: Boolean = subordinatePlayerData.playerInternalData
                            .diplomacyData().relationData.selfWarDataMap
                            .containsKey(warData.warCoreData.opponentId)

                        val isDefensive: Boolean = if (isWarExist) {
                            subordinatePlayerData.playerInternalData.diplomacyData()
                                .relationData.selfWarDataMap
                                .getValue(warData.warCoreData.opponentId).warCoreData.isDefensive
                        } else {
                            false
                        }

                        val isOpponentValid: Boolean = !mutablePlayerData.isLeader(opponentId) &&
                                !mutablePlayerData.isLeaderOrSelf(opponentId)

                        !isSupportIdCorrect || !isOpponentIdCorrect || !isSubordinate ||
                                !isWarExist || !isDefensive || !isOpponentValid
                    } else {
                        true
                    }
                }
            }

        // Add all defensive war of subordinate
        mutablePlayerData.playerInternalData.subordinateIdSet.forEach { subordinateId ->
            if (universeData3DAtPlayer.playerDataMap.containsKey(subordinateId)) {
                universeData3DAtPlayer.get(subordinateId).playerInternalData.diplomacyData()
                    .relationData.selfWarDataMap.forEach { (opponentId, otherWarData) ->
                        val hasWar: Boolean = mutablePlayerData.playerInternalData.diplomacyData()
                            .relationData.hasSubordinateWar(subordinateId, opponentId)

                        val isOpponentValid: Boolean = !mutablePlayerData.isLeader(opponentId) &&
                                !mutablePlayerData.isLeaderOrSelf(opponentId)

                        if (!hasWar && isOpponentValid && otherWarData.warCoreData.isDefensive) {
                            val warData = MutableWarData(
                                warCoreData = DataSerializer.copy(otherWarData.warCoreData),
                            )
                            mutablePlayerData.playerInternalData.diplomacyData().relationData
                                .addSubordinateWar(warData)
                        }
                    }
            }
        }

        // Update war data
        mutablePlayerData.playerInternalData.diplomacyData().relationData.subordinateWarDataMap
            .forEach { (supportId, warDataMap) ->
                warDataMap.forEach { (opponentId, warData) ->
                    val subordinateWarData: WarData = universeData3DAtPlayer
                        .get(supportId).playerInternalData.diplomacyData()
                        .relationData.selfWarDataMap.getValue(opponentId)

                    warData.opponentLeaderIdList.clear()
                    warData.opponentLeaderIdList.addAll(subordinateWarData.opponentLeaderIdList)

                    warData.warCoreData = DataSerializer.copy(subordinateWarData.warCoreData)
                }
            }


        // Remove empty map
        mutablePlayerData.playerInternalData.diplomacyData().relationData.subordinateWarDataMap
            .values.removeAll { it.isEmpty() }
    }

    private fun updateAllyWar(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        // Remove invalid ally war
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyWarDataMap
            .forEach { (supportId, warDataMap) ->
                warDataMap.entries.removeAll { (opponentId, warData) ->
                    val hasPlayer: Boolean = universeData3DAtPlayer.playerDataMap
                        .containsKey(warData.warCoreData.supportId)

                    if (hasPlayer) {
                        val allyPlayerData: PlayerData = universeData3DAtPlayer
                            .get(warData.warCoreData.supportId)

                        val isSupportIdCorrect: Boolean =
                            supportId == warData.warCoreData.supportId
                        val isOpponentIdCorrect: Boolean =
                            opponentId == warData.warCoreData.opponentId

                        val isAlly: Boolean = mutablePlayerData.playerInternalData.diplomacyData()
                            .relationData.isAlly(warData.warCoreData.supportId)

                        // Check start time to prevent war disappeared in after image
                        val isWarExist: Boolean = if (
                            allyPlayerData.int4D.t >= warData.warCoreData.startTime
                        ) {
                            allyPlayerData.playerInternalData.diplomacyData().relationData
                                .selfWarDataMap.containsKey(warData.warCoreData.opponentId)
                        } else {
                            true
                        }

                        val isOpponentValid: Boolean = !mutablePlayerData.isLeader(opponentId) &&
                                !mutablePlayerData.isLeaderOrSelf(opponentId)

                        !isSupportIdCorrect || !isOpponentIdCorrect || !isAlly || !isWarExist ||
                                !isOpponentValid
                    } else {
                        true
                    }
                }
            }

        // Update war data
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyWarDataMap
            .forEach { (supportId, warDataMap) ->
                warDataMap.forEach { (opponentId, warData) ->
                    val allyWarData: WarData =
                        universeData3DAtPlayer.get(supportId)
                            .playerInternalData.diplomacyData().relationData.selfWarDataMap
                            .getValue(opponentId)

                    warData.opponentLeaderIdList.clear()
                    warData.opponentLeaderIdList.addAll(
                        allyWarData.opponentLeaderIdList
                    )

                    warData.warCoreData = DataSerializer.copy(allyWarData.warCoreData)
                }
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allyWarDataMap.values
            .removeAll { it.isEmpty() }
    }

    private fun updateAllySubordinateWar(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
    ) {
        // Remove invalid ally subordinate war
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allySubordinateWarDataMap
            .forEach { (allyId, outerWarDataMap) ->
                outerWarDataMap.forEach { (supportId, innerWarDataMap) ->
                    innerWarDataMap.entries.removeAll { (opponentId, warData) ->
                        val hasPlayer: Boolean = universeData3DAtPlayer.playerDataMap
                            .containsKey(allyId)

                        if (hasPlayer) {
                            val allyPlayerData: PlayerData = universeData3DAtPlayer
                                .get(allyId)

                            val isSupportIdCorrect: Boolean =
                                supportId == warData.warCoreData.supportId
                            val isOpponentIdCorrect: Boolean =
                                opponentId == warData.warCoreData.opponentId

                            val isAlly: Boolean = mutablePlayerData.playerInternalData
                                .diplomacyData().relationData.isAlly(allyId)

                            // Check start time to prevent war disappeared in after image
                            val isWarExist: Boolean = if (
                                allyPlayerData.int4D.t >= warData.warCoreData.startTime
                            ) {
                                allyPlayerData.playerInternalData.diplomacyData().relationData
                                    .hasSubordinateWar(supportId, opponentId)
                            } else {
                                true
                            }

                            val isOpponentValid: Boolean =
                                !mutablePlayerData.isLeader(opponentId) &&
                                        !mutablePlayerData.isLeaderOrSelf(opponentId)

                            !isSupportIdCorrect || !isOpponentIdCorrect || !isAlly || !isWarExist ||
                                    !isOpponentValid
                        } else {
                            true
                        }
                    }
                }
            }

        // Update war data
        mutablePlayerData.playerInternalData.diplomacyData().relationData.allySubordinateWarDataMap
            .forEach { (allyId, outerWarDataMap) ->
                outerWarDataMap.forEach { (supportId, innerWarDataMap) ->
                    innerWarDataMap.forEach { (opponentId, warData) ->
                        val allySubordinateWarData: WarData =
                            universeData3DAtPlayer.get(allyId)
                                .playerInternalData.diplomacyData().relationData
                                .subordinateWarDataMap.getValue(supportId)
                                .getValue(opponentId)

                        warData.opponentLeaderIdList.clear()
                        warData.opponentLeaderIdList.addAll(
                            allySubordinateWarData.opponentLeaderIdList
                        )

                        warData.warCoreData =
                            DataSerializer.copy(allySubordinateWarData.warCoreData)
                    }
                }
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allySubordinateWarDataMap
            .forEach { (_, outerMap) ->
                outerMap.values.removeAll { it.isEmpty() }
            }

        mutablePlayerData.playerInternalData.diplomacyData().relationData.allySubordinateWarDataMap
            .values.removeAll { it.isEmpty() }
    }
}