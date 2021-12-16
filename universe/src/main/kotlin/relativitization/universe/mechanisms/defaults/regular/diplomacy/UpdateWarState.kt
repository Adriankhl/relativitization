package relativitization.universe.mechanisms.defaults.regular.diplomacy

import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlayerData
import relativitization.universe.data.UniverseData3DAtPlayer
import relativitization.universe.data.UniverseSettings
import relativitization.universe.data.commands.Command
import relativitization.universe.data.global.UniverseGlobalData
import relativitization.universe.maths.physics.Intervals
import relativitization.universe.mechanisms.Mechanism

object UpdateWarState : Mechanism() {
    override fun process(
        mutablePlayerData: MutablePlayerData,
        universeData3DAtPlayer: UniverseData3DAtPlayer,
        universeSettings: UniverseSettings,
        universeGlobalData: UniverseGlobalData
    ): List<Command> {
        // Parameters
        val peaceTreatyLength: Int = 15
        val maxWarLength: Int = 100

        // Invalid internal war, is leader or subordinate
        val invalidWarSet: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.filter { (id, _) ->
                mutablePlayerData.isLeaderOrSelf(id) || mutablePlayerData.isSubOrdinateOrSelf(id)
            }.keys

        invalidWarSet.forEach {
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(it)
        }

        val noPlayerWarSet: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.filter { (id, _) ->
                !universeData3DAtPlayer.playerDataMap.containsKey(id)
            }.keys

        // Both have accepted peace or this player accepted peace and other war state has disappeared
        val acceptedPeaceSet: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.filter { (id, warState) ->
                val otherHasWarState: Boolean =
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                        mutablePlayerData.playerId
                    )
                val otherHasProposePeace: Boolean = if (otherHasWarState) {
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.getValue(
                        mutablePlayerData.playerId
                    ).proposePeace
                } else {
                    // Assume the other player has proposed peace if the war state has disappeared,
                    // unless the time from war start time is too short, i.e., the declare war statement
                    // may not reached the other player or this player can view it yet
                    val timeDelay: Int = Intervals.intDelay(
                        universeData3DAtPlayer.get(id).int4D.toInt3D(),
                        mutablePlayerData.int4D.toInt3D(),
                        universeSettings.speedOfLight
                    )
                    val timeDiff: Int = mutablePlayerData.int4D.t - warState.startTime
                    timeDiff > 2 * timeDelay
                }
                (warState.proposePeace) && (!otherHasWarState || otherHasProposePeace)
            }.keys


        // Force the war to stop if the length is too long
        val warTooLongSet: Set<Int> =
            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.filter { (id, warState) ->
                val otherHasWarState: Boolean =
                    universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.containsKey(
                        mutablePlayerData.playerId
                    )
                val otherWarTooLong: Boolean = if (otherHasWarState) {
                    val otherStartTime: Int =
                        universeData3DAtPlayer.get(id).playerInternalData.diplomacyData().warData.warStateMap.getValue(
                            mutablePlayerData.playerId
                        ).startTime
                    mutablePlayerData.int4D.t - otherStartTime > maxWarLength
                } else {
                    true
                }
                (mutablePlayerData.int4D.t - warState.startTime > maxWarLength) && (!otherHasWarState || otherWarTooLong)
            }.keys

        // All player to get peace treaty
        val allPeaceSet: Set<Int> = noPlayerWarSet + acceptedPeaceSet + warTooLongSet
        allPeaceSet.forEach { warId ->
            val warTopLeaderId: Int = mutablePlayerData.playerInternalData.diplomacyData()
                .warData.warStateMap.getValue(warId).warTargetTopLeaderId + warId

            val subordinateSet: Set<Int> = universeData3DAtPlayer.get(warId)
                .playerInternalData.subordinateIdList.toSet()

            val topLeaderSubordinateSet: Set<Int> = if (warTopLeaderId != mutablePlayerData.topLeaderId()) {
                universeData3DAtPlayer.get(warTopLeaderId).playerInternalData.subordinateIdList.toSet() + warTopLeaderId
            } else {
                setOf()
            }


            val peaceTreatyIdSet: Set<Int> = (subordinateSet + topLeaderSubordinateSet).filter {
                it != mutablePlayerData.playerId
            }.toSet()


            peaceTreatyIdSet.forEach {
                mutablePlayerData.playerInternalData.modifierData().diplomacyModifierData.setPeaceTreatyWithLength(
                    it,
                    peaceTreatyLength
                )
            }

            mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.remove(warId)
        }

        // Update war target top leader id
        mutablePlayerData.playerInternalData.diplomacyData().warData.warStateMap.forEach { (id, warState) ->
            warState.warTargetTopLeaderId = universeData3DAtPlayer.get(id).topLeaderId()
        }

        return listOf()
    }
}