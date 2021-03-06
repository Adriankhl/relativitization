package relativitization.universe.ai.defaults.node.other.diplomacy

import relativitization.universe.ai.defaults.consideration.diplomacy.HasPeaceTreatyConsideration
import relativitization.universe.ai.defaults.consideration.diplomacy.RelationConsideration
import relativitization.universe.ai.defaults.consideration.hierarchy.IsTopLeaderConsideration
import relativitization.universe.ai.defaults.consideration.military.InWarConsideration
import relativitization.universe.ai.defaults.consideration.military.InWarWithPlayerConsideration
import relativitization.universe.ai.defaults.consideration.military.LargerMilitaryStrengthConsideration
import relativitization.universe.ai.defaults.consideration.position.DistanceMultiplierConsideration
import relativitization.universe.ai.defaults.score.MilitaryScore
import relativitization.universe.ai.defaults.utils.*
import relativitization.universe.data.MutablePlayerData
import relativitization.universe.data.PlanDataAtPlayer
import relativitization.universe.data.commands.DeclareIndependenceToDirectLeaderCommand
import relativitization.universe.data.commands.DeclareIndependenceToTopLeaderCommand
import relativitization.universe.data.commands.DeclareWarCommand
import relativitization.universe.maths.physics.Int3D
import relativitization.universe.maths.physics.Intervals
import kotlin.random.Random

class DeclareWarReasoner(random: Random) : SequenceReasoner(random) {
    override fun getSubNodeList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<AINode> = listOf(
        DeclareIndependenceReasoner(random),
        SpaceConflictReasoner(random),
    )
}

/**
 * Declare war due to space conflict
 */
class SpaceConflictReasoner(random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {

        val subordinateInt3DSet: Set<Int3D> =
            planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData
                .subordinateIdSet.filter {
                    planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(it)
                }.map {
                    planDataAtPlayer.universeData3DAtPlayer.get(it).int4D.toInt3D()
                }.toSet()

        val allNeighbourInt3DSet: Set<Int3D> = subordinateInt3DSet.flatMap {
            it.getInt3DCubeList(
                halfEdgeLength = 2,
                minX = 0,
                maxX = planDataAtPlayer.universeData3DAtPlayer.universeSettings.xDim - 1,
                minY = 0,
                maxY = planDataAtPlayer.universeData3DAtPlayer.universeSettings.yDim - 1,
                minZ = 0,
                maxZ = planDataAtPlayer.universeData3DAtPlayer.universeSettings.zDim - 1,
            )
        }.toSet()

        val currentPlayer: MutablePlayerData = planDataAtPlayer.getCurrentMutablePlayerData()

        val selfMilitaryScore: Double =
            MilitaryScore.compute(currentPlayer.playerId, planDataAtPlayer)

        // Potential player in conflict
        val conflictPlayerIdSet: Set<Int> = allNeighbourInt3DSet.flatMap {
            planDataAtPlayer.universeData3DAtPlayer.playerId3DMap[it.x][it.y][it.z].values.flatten()
        }.filter {
            !currentPlayer.isLeaderOrSelf(it) && !currentPlayer.isSubOrdinate(it)
        }.toSet()

        val conflictPlayerToTopLeaderIdMap: Map<Int, Int> =
            conflictPlayerIdSet.associateWith { id ->
                planDataAtPlayer.universeData3DAtPlayer.get(id).getLeaderAndSelfIdList()
                    .first { leaderId ->
                        !currentPlayer.isLeaderOrSelf(leaderId) &&
                                !currentPlayer.isSubOrdinate(leaderId) &&
                                planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(
                                    leaderId
                                )
                    }
            }

        val conflictPlayerTopLeaderMilitaryScoreMap: Map<Int, Double> =
            conflictPlayerToTopLeaderIdMap.values.toSet()
                .associateWith {
                    MilitaryScore.compute(it, planDataAtPlayer)
                }

        val declareWarOptionList: List<SpaceConflictDeclareWarOption> = conflictPlayerIdSet.map {
            val targetTopLeaderId: Int = conflictPlayerToTopLeaderIdMap.getValue(it)
            val targetPlayerTopLeaderMilitaryScore: Double =
                conflictPlayerTopLeaderMilitaryScoreMap.getValue(
                    targetTopLeaderId
                )

            val targetPlayerId: Int = if (selfMilitaryScore > targetPlayerTopLeaderMilitaryScore) {
                targetTopLeaderId
            } else {
                it
            }

            SpaceConflictDeclareWarOption(
                targetPlayerId = targetPlayerId,
                selfMilitaryScore = selfMilitaryScore,
                targetPlayerTopLeaderMilitaryScore = conflictPlayerTopLeaderMilitaryScoreMap.getValue(
                    targetTopLeaderId
                ),
                random = random,
            )
        }

        return declareWarOptionList + listOf(
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
                random = random,
            )
        )
    }
}

/**
 * Declare war on target player due to space conflict
 *
 * @property targetPlayerId the id of the target player to declare war
 * @property selfMilitaryScore self military score
 * @property targetPlayerTopLeaderMilitaryScore the military score of the top leader of the target player
 */
class SpaceConflictDeclareWarOption(
    private val targetPlayerId: Int,
    private val selfMilitaryScore: Double,
    private val targetPlayerTopLeaderMilitaryScore: Double,
    random: Random
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> {
        return listOf(
            LargerMilitaryStrengthConsideration(
                selfMilitaryScore = selfMilitaryScore,
                targetMilitaryScore = targetPlayerTopLeaderMilitaryScore,
                rankIfTrue = 1,
                multiplierIfTrue = 0.01,
                bonusIfTrue = 1.0,
                rankIfFalse = 1,
                multiplierIfFalse = 0.00001,
                bonusIfFalse = 1.0
            ),
            InWarConsideration(
                rankIfTrue = 0,
                multiplierIfTrue = 0.01,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.0
            ),
            InWarWithPlayerConsideration(
                otherPlayerId = targetPlayerId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.0
            ),
            RelationConsideration(
                otherPlayerId = targetPlayerId,
                initialMultiplier = 1.0,
                exponent = 0.99,
                rank = 0,
                bonus = 0.0
            ),
            HasPeaceTreatyConsideration(
                otherPlayerId = targetPlayerId,
                rankIfTrue = 0,
                multiplierIfTrue = 0.0,
                bonusIfTrue = 0.0,
                rankIfFalse = 0,
                multiplierIfFalse = 1.0,
                bonusIfFalse = 0.0
            ),
        )
    }

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        // If military score is lower, target the easier player, else target the top player
        planDataAtPlayer.addCommand(
            DeclareWarCommand(
                toId = targetPlayerId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}

class DeclareIndependenceReasoner(random: Random) : DualUtilityReasoner(random) {
    override fun getOptionList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityOption> {
        val hasDirectLeader: Boolean =
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(
                planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
            )

        val hasTopLeader: Boolean =
            planDataAtPlayer.universeData3DAtPlayer.playerDataMap.containsKey(
                planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId()
            )

        val isTopLeader: Boolean = planDataAtPlayer.getCurrentMutablePlayerData().isTopLeader()

        val isTopLeaderDirectLeader: Boolean =
            planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId() ==
                    planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId

        val selfMilitaryScore: Double = MilitaryScore.compute(
            planDataAtPlayer.getCurrentMutablePlayerData().playerId,
            planDataAtPlayer
        )


        val declareIndependenceToDirectLeaderOptionList = if (hasDirectLeader && !isTopLeader) {
            val directLeaderExcludeSelfMilitaryScore: Double = MilitaryScore.computeWithExclusion(
                planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
                planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                planDataAtPlayer,
            )

            listOf(
                DeclareIndependenceToDirectLeaderOption(
                    selfMilitaryScore = selfMilitaryScore,
                    targetMilitaryScore = directLeaderExcludeSelfMilitaryScore,
                    random = random,
                )
            )
        } else {
            listOf()
        }

        val declareIndependenceToTopLeaderOptionList =
            if (hasTopLeader && !isTopLeader && !isTopLeaderDirectLeader) {
                val topLeaderExcludeSelfMilitaryScore: Double = MilitaryScore.computeWithExclusion(
                    planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                    planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                    planDataAtPlayer,
                )

                listOf(
                    DeclareIndependenceToTopLeaderOption(
                        selfMilitaryScore = selfMilitaryScore,
                        targetMilitaryScore = topLeaderExcludeSelfMilitaryScore,
                        random = random,
                    )
                )
            } else {
                listOf()
            }

        return declareIndependenceToDirectLeaderOptionList + declareIndependenceToTopLeaderOptionList + listOf(
            DoNothingDualUtilityOption(
                rank = 1,
                multiplier = 1.0,
                bonus = 1.0,
                random = random,
            )
        )
    }
}

/**
 * Declare war and independence to direct leader
 */
class DeclareIndependenceToDirectLeaderOption(
    private val selfMilitaryScore: Double,
    private val targetMilitaryScore: Double,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        LargerMilitaryStrengthConsideration(
            selfMilitaryScore = selfMilitaryScore,
            targetMilitaryScore = targetMilitaryScore,
            rankIfTrue = 1,
            multiplierIfTrue = 0.01,
            bonusIfTrue = 1.0,
            rankIfFalse = 1,
            multiplierIfFalse = 0.00001,
            bonusIfFalse = 1.0
        ),
        InWarConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.01,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        InWarWithPlayerConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        RelationConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
            initialMultiplier = 1.0,
            exponent = 0.99,
            rank = 0,
            bonus = 0.0
        ),
        DistanceMultiplierConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
            minDistance = Intervals.sameCubeIntDistance() + 1,
            initialMultiplier = 1.0,
            exponent = 1.2,
            rank = 0,
            bonus = 0.0
        ),
        IsTopLeaderConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        HasPeaceTreatyConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            DeclareIndependenceToDirectLeaderCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().playerInternalData.directLeaderId,
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}

/**
 * Declare war and independence to direct leader
 */
class DeclareIndependenceToTopLeaderOption(
    private val selfMilitaryScore: Double,
    private val targetMilitaryScore: Double,
    random: Random,
) : DualUtilityOption(random) {
    override fun getConsiderationList(
        planDataAtPlayer: PlanDataAtPlayer,
        planState: PlanState
    ): List<DualUtilityConsideration> = listOf(
        LargerMilitaryStrengthConsideration(
            selfMilitaryScore = selfMilitaryScore,
            targetMilitaryScore = targetMilitaryScore,
            rankIfTrue = 1,
            multiplierIfTrue = 0.01,
            bonusIfTrue = 1.0,
            rankIfFalse = 1,
            multiplierIfFalse = 0.00001,
            bonusIfFalse = 1.0
        ),
        InWarConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.01,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        InWarWithPlayerConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        RelationConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
            initialMultiplier = 1.0,
            exponent = 0.99,
            rank = 0,
            bonus = 0.0
        ),
        DistanceMultiplierConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
            minDistance = Intervals.sameCubeIntDistance() + 1,
            initialMultiplier = 1.0,
            exponent = 1.2,
            rank = 0,
            bonus = 0.0
        ),
        IsTopLeaderConsideration(
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
        HasPeaceTreatyConsideration(
            otherPlayerId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
            rankIfTrue = 0,
            multiplierIfTrue = 0.0,
            bonusIfTrue = 0.0,
            rankIfFalse = 0,
            multiplierIfFalse = 1.0,
            bonusIfFalse = 0.0
        ),
    )

    override fun updatePlan(planDataAtPlayer: PlanDataAtPlayer, planState: PlanState) {
        planDataAtPlayer.addCommand(
            DeclareIndependenceToTopLeaderCommand(
                toId = planDataAtPlayer.getCurrentMutablePlayerData().topLeaderId(),
                fromId = planDataAtPlayer.getCurrentMutablePlayerData().playerId,
                fromInt4D = planDataAtPlayer.getCurrentMutablePlayerData().int4D.toInt4D(),
            )
        )
    }
}