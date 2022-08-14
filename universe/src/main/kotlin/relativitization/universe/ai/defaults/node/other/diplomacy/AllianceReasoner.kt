package relativitization.universe.ai.defaults.node.other.diplomacy

import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.consideration.diplomacy.TooManyAllyConsideration
import relativitization.universe.ai.defaults.consideration.military.InDefensiveWarConsideration
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.PlayerData
import relativitization.universe.data.commands.AddEventCommand
import relativitization.universe.data.commands.RemoveAllyCommand
import relativitization.universe.data.components.defaults.diplomacy.war.MutableWarData
import relativitization.universe.data.components.diplomacyData
import relativitization.universe.data.events.CallAllyToSubordinateWarEvent
import relativitization.universe.data.events.CallAllyToWarEvent
import relativitization.universe.data.events.ProposeAllianceEvent
import relativitization.universe.maths.physics.Int3D
import kotlin.random.Random

class AllianceReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return listOf(
            RemoveAllyReasoner(random),
            ProposeAllianceReasoner(random),
            CallAllyToWarReasoner(random),
            CallAllyToSubordinateWarReasoner(random),
        )
    }
}

/**
 * Consider breaking alliance
 */
class RemoveAllyReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        // Filter out non-existing (dead) ally
        val allyIdList: List<Int> = planDataAtPlayer.getCurrentMutablePlayerData()
            .playerInternalData.diplomacyData().relationData.allyMap.keys.filter {
                planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
            }

        return allyIdList.map {
            RemoveSpecificAllyReasoner(it, random)
        }
    }
}

class RemoveSpecificAllyReasoner(
    private val otherPlayerId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            RemoveSpecificAllyOption(otherPlayerId),
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
            )
        )
    }
}

class RemoveSpecificAllyOption(
    private val otherPlayerId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            TooManyAllyConsideration(
                playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                targetNumAlly = 3,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.01,
            ),
            TooManyAllyConsideration(
                playerId = otherPlayerId,
                targetNumAlly = 3,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.01,
            ),
            RelationConsideration(
                otherPlayerId = otherPlayerId,
                initialMultiplier = 1.0,
                exponent = 0.99,
                rank = 1,
                bonus = 0.0,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            RemoveAllyCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                targetPlayerId = otherPlayerId,
            )
        )
    }
}

/**
 * Consider proposing alliance, number of alliance should be smaller than or equal to 2
 */
class ProposeAllianceReasoner(
    private val random: Random,
) : AINode() {
    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val playerData: MutablePlayerData = planDataAtPlayer.getCurrentMutablePlayerData()

        // Only search for new ally if current number of ally is smaller than 2
        if (playerData.playerInternalData.diplomacyData().relationData.allyMap.size <= 2) {
            // Randomly select a cube to search for alliance
            val xSearch: Int = random.nextInt(
                0,
                planDataAtPlayer.universeData3DAtPlayer.universeSettings.xDim
            )
            val ySearch: Int = random.nextInt(
                0,
                planDataAtPlayer.universeData3DAtPlayer.universeSettings.yDim
            )
            val zSearch: Int = random.nextInt(
                0,
                planDataAtPlayer.universeData3DAtPlayer.universeSettings.zDim
            )

            val playerList: List<PlayerData> = planDataAtPlayer
                .universeData3DAtPlayer.get(Int3D(xSearch, ySearch, zSearch)).values.flatten()

            val suitableAlly: List<PlayerData> = playerList.filter {
                !playerData.isLeaderOrSelf(it.playerId) &&
                        !playerData.isSubOrdinate(it.playerId) &&
                        !playerData.playerInternalData.diplomacyData().relationData.isEnemy(it.playerId) &&
                        (it.playerInternalData.diplomacyData().relationData.allyMap.size <= 2)
            }

            if (suitableAlly.isNotEmpty()) {
                val targetAlly: PlayerData = suitableAlly.shuffled(random).first()

                val event = ProposeAllianceEvent(
                    toId = targetAlly.playerId,
                    fromId = playerData.playerId
                )

                planDataAtPlayer.addCommand(
                    AddEventCommand(
                        event = event,
                        fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
                    )
                )
            }
        }
    }
}

/**
 * Consider calling ally to self war
 */
class CallAllyToWarReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        return planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
            .relationData.selfWarDataMap.keys.flatMap { opponentId ->
                planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
                    .relationData.allyMap.keys.filter { allyId ->
                        !planState.isCommandSentRecently(allyId, planDataAtPlayer)
                    }.map { allyId ->
                        CallSpecificAllyToSpecificWarReasoner(opponentId, allyId, random)
                    }
            }
    }
}

class CallSpecificAllyToSpecificWarReasoner(
    private val opponentId: Int,
    private val allyId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            CallAllyToWarOption(opponentId = opponentId, allyId = allyId),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}

class CallAllyToWarOption(
    private val opponentId: Int,
    private val allyId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            InDefensiveWarConsideration(
                playerId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                warTargetId = opponentId,
                rankIfTrue = 2,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.5,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val event = CallAllyToWarEvent(
            toId = allyId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            warTargetId = opponentId
        )

        planDataAtPlayer.addCommand(
            AddEventCommand(
                event = event,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}

/**
 * Consider calling ally to subordinate war
 */
class CallAllyToSubordinateWarReasoner(private val random: Random) : SequenceReasoner() {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> {
        val subordinateWarDataMap: Map<Int, Map<Int, MutableWarData>> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
                .relationData.subordinateWarDataMap

        return subordinateWarDataMap.flatMap { (subordinateId, opponentMap) ->
            opponentMap.keys.flatMap {opponentId ->
                planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.diplomacyData()
                    .relationData.allyMap.keys.filter { allyId ->
                        !planState.isCommandSentRecently(allyId, planDataAtPlayer)
                    }.map { allyId ->
                        CallSpecificAllyToSpecificSubordinateWarReasoner(
                            subordinateId = subordinateId,
                            opponentId = opponentId,
                            allyId = allyId,
                            random = random
                        )
                    }
            }
        }
    }
}

class CallSpecificAllyToSpecificSubordinateWarReasoner(
    private val subordinateId: Int,
    private val opponentId: Int,
    private val allyId: Int,
    random: Random,
) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        return listOf(
            CallAllyToSubordinateWarOption(
                subordinateId = subordinateId,
                opponentId = opponentId,
                allyId = allyId
            ),
            DoNothingDualUtilityOption(rank = 1, multiplier = 1.0, bonus = 1.0),
        )
    }
}

class CallAllyToSubordinateWarOption(
    private val subordinateId: Int,
    private val opponentId: Int,
    private val allyId: Int,
) : DualUtilityOption() {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            InDefensiveWarConsideration(
                playerId = subordinateId,
                warTargetId = opponentId,
                rankIfTrue = 1,
                multiplierIfTrue = 1.0,
                bonusIfTrue = 0.05,
                rankIfFalse = 1,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.005,
            )
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        val event = CallAllyToSubordinateWarEvent(
            toId = allyId,
            fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            subordinateId = subordinateId,
            warTargetId = opponentId,
        )

        planDataAtPlayer.addCommand(
            AddEventCommand(
                event = event,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}