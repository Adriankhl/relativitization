package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.components.defaults.diplomacy.MutableWarStateData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.Mechanism
import relativitization.universe.utils.RelativitizationLogManager

object UpdateWarState : Mechanism() {
    private val logger = RelativitizationLogManager.getLogger()

    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        val peaceTreatyLength: Int = 15
        val maxWarLength: Int = 100

        // Clear weird self war
        if (mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                mutablePlayerData.playerId
            )
        ) {
            logger.error("Player ${mutablePlayerData.playerId} is in war with self")
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(mutablePlayerData.playerId)
        }

        // Invalid internal war, is leader or subordinate
        val invalidWarSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData().warData
            .warStateMap.filter { (id, _) ->
                mutablePlayerData.isLeader(id) || mutablePlayerData.isSubOrdinate(id)
            }.keys

        // Remove the war if the player does not exist, i.e., dead
        val playerNotExistSet: Set<Int> = mutablePlayerData.playerInternalData.diplomacyData().warData
            .warStateMap.filter { (id, _) ->
                !universeData3DAtPlayer.playerDataMap.containsKey(id)
            }.keys

        val validWarStateMap: Map<Int, MutableWarStateData> = mutablePlayerData.playerInternalData.diplomacyData()
            .warData.warStateMap.filterKeys { id ->
                !invalidWarSet.contains(id) && !playerNotExistSet.contains(id)
            }

        // Filter out those who should have received the war declaration and the information also has traveled back
        val warStateOldEnoughSet: Set<Int> = validWarStateMap.filter { (id, warState) ->
            val timeDelay: Int = Intervals.intDelay(
                universeData3DAtPlayer.get(id).int4D.toInt3D(),
                mutablePlayerData.int4D.toInt3D(),
                universeSettings.speedOfLight
            )
            val timeDiff: Int = mutablePlayerData.int4D.t - warState.startTime

            // Offensive war need to wait the information to travel back, so 2 times the delay
            if (warState.isOffensive) {
                timeDiff >= 2 * timeDelay
            } else {
                timeDiff >= timeDelay
            }
        }.keys

        // Both have accepted peace or this player accepted peace and other war state has disappeared
        val acceptedPeaceOrNoWarStateSet: Set<Int> = warStateOldEnoughSet.filter { id ->
            val otherHasWarState: Boolean = universeData3DAtPlayer.get(id).playerInternalData.diplomacyData()
                .warData.warStateMap.containsKey(mutablePlayerData.playerId)

            val bothProposedPeace: Boolean = if (otherHasWarState) {
                val thisProposedPeace: Boolean = mutablePlayerData.playerInternalData.diplomacyData()
                    .warData.warStateMap.getValue(id).proposePeace

                val otherProposedPeace: Boolean = universeData3DAtPlayer.get(id).playerInternalData.diplomacyData()
                    .warData.warStateMap.getValue(
                        mutablePlayerData.playerId
                    ).proposePeace

                otherProposedPeace && thisProposedPeace
            } else {
                false
            }

            !otherHasWarState || bothProposedPeace
        }.toSet()

        // Force the war to stop if the length is too long
        val warTooLongSet: Set<Int> = validWarStateMap.filter { (id, warState) ->
            val otherHasWarState: Boolean = universeData3DAtPlayer.get(id).playerInternalData
                .diplomacyData().warData.warStateMap.containsKey(
                    mutablePlayerData.playerId
                )
            val otherWarTooLong: Boolean = if (otherHasWarState) {
                val otherStartTime: Int = universeData3DAtPlayer.get(id).playerInternalData
                    .diplomacyData().warData
                    .warStateMap.getValue(
                        mutablePlayerData.playerId
                    ).startTime
                mutablePlayerData.int4D.t - otherStartTime > maxWarLength
            } else {
                true
            }
            (mutablePlayerData.int4D.t - warState.startTime > maxWarLength) &&
                    (!otherHasWarState || otherWarTooLong)
        }.keys

        // All player to get peace treaty
        val allPeaceSet: Set<Int> = invalidWarSet + playerNotExistSet + acceptedPeaceOrNoWarStateSet + warTooLongSet
        allPeaceSet.forEach { warId ->
            val subordinateSet: Set<Int> = if (playerNotExistSet.contains(warId)) {
                setOf()
            } else {
                universeData3DAtPlayer.get(warId).playerInternalData.subordinateIdSet
            }

            // If this is an offensive war, add the enemy top leader and subordinate to peace treaty
            val isOffensiveWar: Boolean = mutablePlayerData.playerInternalData.diplomacyData()
                .warData.warStateMap.getValue(warId).isOffensive

            val topLeaderSubordinateSet: Set<Int> = if (isOffensiveWar) {
                val warTopLeaderId: Int = mutablePlayerData.playerInternalData.diplomacyData()
                    .warData.warStateMap.getValue(warId).warTargetTopLeaderId

                if (mutablePlayerData.isLeaderOrSelf(warTopLeaderId) &&
                    universeData3DAtPlayer.playerDataMap.containsKey(warTopLeaderId)
                ) {
                    universeData3DAtPlayer.get(warTopLeaderId).getSubordinateAndSelfIdSet()
                } else {
                    setOf()
                }
            } else {
                setOf()
            }

            val peaceTreatyIdSet: Set<Int> = (subordinateSet + topLeaderSubordinateSet).filter {
                // If the target is not a subordinate, don't add peace treaty to subordinate
                if (mutablePlayerData.isSubOrdinateOrSelf(warId)) {
                    true
                } else {
                    !mutablePlayerData.isSubOrdinateOrSelf(it)
                }
            }.toSet()

            peaceTreatyIdSet.forEach {
                mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData
                    .setPeaceTreatyWithLength(
                        it,
                        peaceTreatyLength
                    )
            }

            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(warId)
        }

        // Update war target top leader id
        mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap
            .forEach { (id, warState) ->
                warState.warTargetTopLeaderId = universeData3DAtPlayer.get(
                    id
                ).getLeaderAndSelfIdList().first {
                    !mutablePlayerData.isLeaderOrSelf(it)
                }
            }

        return listOf()
    }
}